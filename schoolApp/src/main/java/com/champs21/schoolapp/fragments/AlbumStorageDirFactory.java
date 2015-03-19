package com.champs21.schoolapp.fragments;
import java.io.File;

public abstract class AlbumStorageDirFactory {
	public abstract File getAlbumStorageDir(String albumName);
}
