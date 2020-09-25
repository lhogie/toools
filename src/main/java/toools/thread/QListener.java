package toools.thread;

public interface QListener<E>
{
	void newElement(Q q, E e);
}
