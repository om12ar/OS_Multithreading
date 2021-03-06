package vfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Disk {
	Tree tree;
	int diskSize;
	int numOfBlocks;
	RandomAccessFile drive;
	final String PERMISSIONS_FILE = "capabilities.txt";
	TreeMap<String, TreeMap<String, String> > permissionsList;
	StringBuilder freeSpaceManager;

	public Disk(int numOfBlocks) throws IOException {

		tree = new Tree();
		diskSize = numOfBlocks * 1024;
		this.numOfBlocks = numOfBlocks;
		drive = new RandomAccessFile("VFSD.vfs", "rw");

		drive.setLength(diskSize);
		permissionsList = new TreeMap<>();
		String temp = String.format("%" + numOfBlocks + "s", "").replaceAll(" ", "0");

		freeSpaceManager = new StringBuilder(temp);	
		ReadPermissions();

	}

	
	public void DisplayStatus() {

		System.out.println("Disk Status:");
		System.out.println("Total Size: " + (numOfBlocks * 1024) + "KBs");
		System.out.println("Free Space: " + (getFreeBlocks() * 1024) + "KBs");
		System.out.println("Allocated Space: " + (getAllocatedBlocks() * 1024) + "KBs");
		System.out.println("No. of Free Blocks: " + getFreeBlocks());
		System.out.println("No. of Allocated Blocks: " + getAllocatedBlocks());
	}

	public void DisplayTreeStructure() {
		tree.printTree(tree.getRootNode(), 0);
	}

	public void CFolder(String pathString) {

		ArrayList<String> path = new ArrayList<>();

		path.addAll(Arrays.asList(pathString.split("/")));

		FolderModel foldermodel = new FolderModel(path.get(path.size() - 1), pathString);

		tree.createFolder(path, foldermodel);

	}

	public void CFile(String pathString, int fileSize) {

	}

	public void DFile(String pathString) {

	}

	public void DFolder(String pathString) {

	}

	public int getFreeBlocks() {

		int counter = 0;
		for (int i = 0; i < freeSpaceManager.length(); i++) {

			if (freeSpaceManager.charAt(i) == '0') {
				counter++;
			}
		}

		return counter;
	}

	public int getAllocatedBlocks() {

		int counter = 0;
		for (int i = 0; i < freeSpaceManager.length(); i++) {

			if (freeSpaceManager.charAt(i) == '1') {
				counter++;
			}
		}

		return counter;
	}

	public void printFreeSpaceManager() {

		System.out.println(freeSpaceManager.toString());
	}

	public void SaveDiskToFile() {

	}

	public void ReadDiskFromFile() {

	}

	public void chmod(String folderPath, String user, String permissions) {

		if (!ProtectionLayer.currentUser.equals("admin")) {
			System.out.println("Permission denied.");
		} else if (ProtectionLayer.users.get(user) == null) {
			System.out.println("User doesn't exist.");
		} else {

			ArrayList<String> path = new ArrayList<>();
			path.addAll(Arrays.asList(folderPath.split("/")));
			Node<FolderModel> node = new Node<>();

			node = tree.findFolder(path);

			if (node != null) {

				node.getData().setPermissions(user, permissions);
			/*	ArrayList<String> temp = new ArrayList<>();
				temp.add(user);
				temp.add(permissions);
				permissionsList.put(folderPath, temp);*/
				System.out.println(node.getData().getName() + node.getData().usersPermissions.toString());
			}

		}
	}
	
	private void ReadPermissions() {
		File file = new File(PERMISSIONS_FILE);
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			permissionsList = (TreeMap<String, TreeMap<String, String>>) ois.readObject();
		} catch (ClassNotFoundException | IOException e) {
			permissionsList= new TreeMap<>();
		}
		
		tree.setAllPermissions(permissionsList);
		
	}

	protected void writePermissionToFIle() {
		permissionsList = tree.getAllPermissions(tree.getRootNode());
		File file = new File(PERMISSIONS_FILE);
		FileOutputStream fos ;
		try {
			fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(permissionsList);
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
