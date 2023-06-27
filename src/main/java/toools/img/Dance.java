package toools.img;

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import toools.io.file.Directory;
import toools.io.file.RegularFile;
import toools.io.ser.JavaSerializer;
import toools.thread.IndependantObjectMultiThreadProcessing;
import toools.thread.Threads;

public class Dance
{
	public static void main(String[] args)
			throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException
	{
		System.out.println("euhuhu");
		Directory d = new Directory(args[0]);
		List<RegularFile> mp3Files = d.getChildRegularFiles().stream().filter(f -> f.getName().endsWith(".mp3")).toList();

		if ( ! mp3Files.isEmpty())
			throw new IllegalStateException("no mp3 files in " + d);

		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;

		List<ImageIcon> imgs = load(d, width, height);

		JButton l = new JButton();
		l.addKeyListener(new KeyAdapter()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
				char c = Character.toLowerCase(e.getKeyChar());

				if ('a' <= c && c <= 'z')
				{
					int i = "abcdefghijklmnopqrstuvwxyz".indexOf(c) % imgs.size();
					show(l, imgs.get(i));
				}
			}
		});

		JFrame f = new JFrame();
		f.setContentPane(l);
//		f.setUndecorated(true);
		f.setSize(width, height);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		ge.getScreenDevices()[0].setFullScreenWindow(f);
		// f.createBufferStrategy(2);

		
		System.out.println("visible");
		
		while (imgs.isEmpty())
		{
			Threads.sleepMs(100);
		}
		
		f.setVisible(true);
		show(l, imgs.get(0));
	}

	static List<ImageIcon> load(Directory d, int width, int height) throws IOException
	{
		RegularFile binFile = d.getChildRegularFile("images.bin");

		if (false)//binFile.exists())
		{
			return (List<ImageIcon>) new JavaSerializer().fromBytes(binFile.getContent());
		}
		else
		{
			List<ImageIcon> imgs = Collections.synchronizedList(new ArrayList<>());
			List<RegularFile> imgFiles = d.getChildRegularFiles().stream().filter(f -> f.getName().endsWith(".jpg")).toList(); 

			new Thread(new Runnable()
			{

				@Override
				public void run()
				{
					new IndependantObjectMultiThreadProcessing<RegularFile>(imgFiles)
					{

						@Override
						protected void process(RegularFile f) throws Throwable
						{
							Image i = Utilities
									.toBufferedImage(Toolkit.getDefaultToolkit()
											.createImage(f.getContent()))
									.getScaledInstance(width, height,
											BufferedImage.SCALE_FAST);

							synchronized (imgs)
							{
								imgs.add(new ImageIcon(i));
								System.out.println("add");
							}
						}
					};
					
					
						binFile.setContent(new JavaSerializer().toBytes(imgs));
					
				}
			}).start();

			while (imgs.isEmpty())
			{
				Threads.sleepMs(100);
			}
			
			return imgs;
		}
	}

	private static void show(JButton l, ImageIcon imageIcon)
	{
		l.setIcon(imageIcon);
	}
}
