/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 
Aurelien Lancin (Coati research team, Inria)
Christian Glacet (LaBRi, Bordeaux)
David Coudert (Coati research team, Inria)
Fabien Crequis (Coati research team, Inria)
Grégory Morel (Coati research team, Inria)
Issam Tahiri (Coati research team, Inria)
Julien Fighiera (Aoste research team, Inria)
Laurent Viennot (Gang research-team, Inria)
Michel Syska (I3S, Université Cote D'Azur)
Nathann Cohen (LRI, Saclay) 
Julien Deantoin (I3S, Université Cote D'Azur, Saclay) 

*/
 
 package toools.collections;

/**
 * Implements a top-down splay tree. Available at
 * http://www.link.cs.cmu.edu/splay/ Author: Danny Sleator <sleator@cs.cmu.edu>
 * This code is in the public domain.
 */

public class SPlayTree
{
	static class BinaryNode
	{
		BinaryNode(Comparable theKey)
		{
			key = theKey;
			left = right = null;
		}

		Comparable key; // The data in the node
		BinaryNode left; // Left child
		BinaryNode right; // Right child
	}

	private BinaryNode root;

	public SPlayTree()
	{
		root = null;
	}

	/**
	 * Insert into the tree.
	 * 
	 * @param key
	 *            the item to insert.
	 * @throws DuplicateItemException
	 *             if x is already present.
	 */
	public void insert(Comparable key)
	{
		BinaryNode n;
		int c;
		if (root == null)
		{
			root = new BinaryNode(key);
			return;
		}
		splay(key);
		if ((c = key.compareTo(root.key)) == 0)
		{
			// throw new DuplicateItemException(x.toString());
			return;
		}
		n = new BinaryNode(key);
		if (c < 0)
		{
			n.left = root.left;
			n.right = root;
			root.left = null;
		}
		else
		{
			n.right = root.right;
			n.left = root;
			root.right = null;
		}
		root = n;
	}

	/**
	 * Remove from the tree.
	 * 
	 * @param x
	 *            the item to remove.
	 * @throws ItemNotFoundException
	 *             if x is not found.
	 */
	public void remove(Comparable key)
	{
		BinaryNode x;
		splay(key);
		if (key.compareTo(root.key) != 0)
		{
			// throw new ItemNotFoundException(x.toString());
			return;
		}
		// Now delete the root
		if (root.left == null)
		{
			root = root.right;
		}
		else
		{
			x = root.right;
			root = root.left;
			splay(key);
			root.right = x;
		}
	}

	/**
	 * Find the smallest item in the tree.
	 */
	public Comparable findMin()
	{
		BinaryNode x = root;
		if (root == null)
			return null;
		while (x.left != null)
			x = x.left;
		splay(x.key);
		return x.key;
	}

	/**
	 * Find the largest item in the tree.
	 */
	public Comparable findMax()
	{
		BinaryNode x = root;
		if (root == null)
			return null;
		while (x.right != null)
			x = x.right;
		splay(x.key);
		return x.key;
	}

	/**
	 * Find an item in the tree.
	 */
	public Comparable find(Comparable key)
	{
		if (root == null)
			return null;
		splay(key);
		if (root.key.compareTo(key) != 0)
			return null;
		return root.key;
	}

	/**
	 * Test if the tree is logically empty.
	 * 
	 * @return true if empty, false otherwise.
	 */
	public boolean isEmpty()
	{
		return root == null;
	}

	/**
	 * this method just illustrates the top-down method of implementing the
	 * move-to-root operation
	 */
	private void moveToRoot(Comparable key)
	{
		BinaryNode l, r, t, y;
		l = r = header;
		t = root;
		header.left = header.right = null;
		for (;;)
		{
			if (key.compareTo(t.key) < 0)
			{
				if (t.left == null)
					break;
				r.left = t; /* link right */
				r = t;
				t = t.left;
			}
			else if (key.compareTo(t.key) > 0)
			{
				if (t.right == null)
					break;
				l.right = t; /* link left */
				l = t;
				t = t.right;
			}
			else
			{
				break;
			}
		}
		l.right = t.left; /* assemble */
		r.left = t.right;
		t.left = header.right;
		t.right = header.left;
		root = t;
	}

	private static BinaryNode header = new BinaryNode(null); // For splay

	/**
	 * Internal method to perform a top-down splay.
	 * 
	 * splay(key) does the splay operation on the given key. If key is in the
	 * tree, then the BinaryNode containing that key becomes the root. If key is
	 * not in the tree, then after the splay, key.root is either the greatest
	 * key < key in the tree, or the lest key > key in the tree.
	 * 
	 * This means, among other things, that if you splay with a key that's
	 * larger than any in the tree, the rightmost node of the tree becomes the
	 * root. This property is used in the delete() method.
	 */

	private void splay(Comparable key)
	{
		BinaryNode l, r, t, y;
		l = r = header;
		t = root;
		header.left = header.right = null;
		for (;;)
		{
			if (key.compareTo(t.key) < 0)
			{
				if (t.left == null)
					break;
				if (key.compareTo(t.left.key) < 0)
				{
					y = t.left; /* rotate right */
					t.left = y.right;
					y.right = t;
					t = y;
					if (t.left == null)
						break;
				}
				r.left = t; /* link right */
				r = t;
				t = t.left;
			}
			else if (key.compareTo(t.key) > 0)
			{
				if (t.right == null)
					break;
				if (key.compareTo(t.right.key) > 0)
				{
					y = t.right; /* rotate left */
					t.right = y.left;
					y.left = t;
					t = y;
					if (t.right == null)
						break;
				}
				l.right = t; /* link left */
				l = t;
				t = t.right;
			}
			else
			{
				break;
			}
		}
		l.right = t.left; /* assemble */
		r.left = t.right;
		t.left = header.right;
		t.right = header.left;
		root = t;
	}

	// test code stolen from Weiss
	public static void main(String[] args)
	{
		SPlayTree t = new SPlayTree();
		final int NUMS = 40000;
		final int GAP = 307;

		System.out.println("Checking... (no bad output means success)");

		for (int i = GAP; i != 0; i = (i + GAP) % NUMS)
			t.insert(new Integer(i));
		System.out.println("Inserts complete");

		for (int i = 1; i < NUMS; i += 2)
			t.remove(new Integer(i));
		System.out.println("Removes complete");

		if (((Integer) (t.findMin())).intValue() != 2
				|| ((Integer) (t.findMax())).intValue() != NUMS - 2)
			System.out.println("FindMin or FindMax error!");

		for (int i = 2; i < NUMS; i += 2)
			if (((Integer) t.find(new Integer(i))).intValue() != i)
				System.out.println("Error: find fails for " + i);

		for (int i = 1; i < NUMS; i += 2)
			if (t.find(new Integer(i)) != null)
				System.out.println("Error: Found deleted item " + i);
	}

}
