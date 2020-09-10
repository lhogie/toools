package toools.io.fast_input_stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

public final class PagingInputStream extends InputStream implements Iterable<Page> {
	ArrayBlockingQueue q = new ArrayBlockingQueue(1);
	private Page[] page = new Page[2];
	private int currentPageIndex = 0;
	private InputStream in;
	private boolean eof;
	private Thread thread;

	public PagingInputStream(InputStream in) {
		this(in, 8 * 1024);
	}

	public PagingInputStream(InputStream in, int pageSize) {
		this.in = in;
		this.page[0] = new Page(pageSize);
		this.page[1] = new Page(pageSize);

		thread = new Thread(() -> {
			try {
				while ( ! eof) {
					Page otherPage = page[ - currentPageIndex + 1];
					int nbBytesRead = in.read(otherPage.buf, 0, otherPage.buf.length);

					if (nbBytesRead == 0) {
						continue;
					}
					else if (nbBytesRead == - 1) {
						eof = true;
						otherPage.len = 0;
						otherPage.cursor = 0;
					}
					else {
						otherPage.len = nbBytesRead;
						otherPage.cursor = 0;
					}
					
					try {
						q.put("page ready");
					}
					catch (InterruptedException e) {
						throw new IllegalStateException(e);
					}

				}

			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		thread.start();
	}

	@Override
	public int read(byte[] target, int off, int len) throws IOException {
		if (eof()) {
			return - 1;
		}

		int avail = page[currentPageIndex].available();

		if (avail == 0) {
			return in.read(target, 0, len);
		}
		// if ask less than available
		else if (len < avail) {
			// give what is asked for
			System.arraycopy(page[currentPageIndex].buf, page[currentPageIndex].cursor,
					target, off, len);
			page[currentPageIndex].cursor += len;
			return len;
		}
		else {
			// give all available data
			System.arraycopy(page[currentPageIndex].buf, page[currentPageIndex].cursor,
					target, off, avail);
			page[currentPageIndex].cursor += avail;

			// then read
			int read = in.read(target, off + avail, len - avail);
			return read == - 1 ? avail : avail + read;
		}
	}

	@Override
	public int read() {
		return eof() ? - 1 : page[currentPageIndex].next();
	}

	@Override
	public int available() {
		return page[currentPageIndex].available();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	public boolean eof() {
		if (eof) {
			return true;
		}
		else if (page[currentPageIndex].available() > 0) {
			return false;
		}
		else {
			flip();
			return eof();
		}
	}

	private void flip() {
		if (page[currentPageIndex].available() > 0)
			throw new IllegalStateException("cannot flip page which unseen data");

		// waits for next page to be ready
		try {
			q.take();
		}
		catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}

		// switch page
		currentPageIndex = - currentPageIndex + 1;
	}

	@Override
	public Iterator<Page> iterator() {
		return new Iterator<Page>() {
			boolean flipped = false;

			@Override
			public boolean hasNext() {
				if ( ! flipped) {
					flip();
					flipped = true;
				}

				return ! eof;
			}

			@Override
			public Page next() {
				flipped = false;
				return page[currentPageIndex];
			}
		};
	}

	public String readLine() {
		if (eof()) {
			return null;
		}

		StringBuilder b = new StringBuilder();

		while (true) {
			int i = read();

			if (i == '\n' || i == - 1) {
				return b.toString();
			}
			else {
				b.append((char) i);
			}
		}
	}

	public Iterable<String> lines() {

		return () -> new Iterator<String>() {
			String next = readLine();

			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public String next() {
				String tmp = next;
				next = readLine();
				return tmp;
			}
		};
	}

}
