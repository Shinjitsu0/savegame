package savegame;

import java.io.*;
import java.util.*;
import java.util.zip.*;


public class Main {

  static void saveGame(String path, GameProgress gp) {
    try {
      new File(path).createNewFile();
    } catch (IOException ex) {
      System.out.println(ex.getMessage());
    }

    try (FileOutputStream fos = new FileOutputStream(path);
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
      oos.writeObject(gp);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }

  static void zipFiles(String zipPath, String savePath) {
    File dir = new File(savePath);
    List<File> list = new ArrayList<>();

    for (File file : dir.listFiles()) {
      if (file.isFile() && !file.getName().endsWith("zip")) {
        list.add(file);
      }
    }

    try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipPath))) {
      for (File s : list) {
        try (FileInputStream fis = new FileInputStream(s)) {
          ZipEntry entry = new ZipEntry("packed_" + s.getName());
          zout.putNextEntry(entry);
          byte[] buffer = new byte[fis.available()];
          fis.read(buffer);
          zout.write(buffer);
          zout.closeEntry();
        } catch (Exception ex) {
          System.out.println(ex);
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }

  static void deleteIfNotZip(String path) {
    for (File myFile : new File(path).listFiles()) {
      if (myFile.getName().endsWith("dat")) {
        myFile.delete();
      }
    }
  }

  static void openZip(String zipPath, String dstPath) {
    try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipPath))) {
      ZipEntry entry;
      while ((entry = zin.getNextEntry()) != null) {
        FileOutputStream fout = new FileOutputStream(dstPath + entry.getName());
        for (int c = zin.read(); c != -1; c = zin.read()) {
          fout.write(c);
        }
        fout.flush();
        zin.closeEntry();
        fout.close();
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }

  static void openProgress(String saveDstFile) {
    GameProgress gameProgress = null;

    try (FileInputStream fis = new FileInputStream(saveDstFile);
        ObjectInputStream ois = new ObjectInputStream(fis)) {
      gameProgress = (GameProgress) ois.readObject();
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }

    System.out.println(gameProgress);
  }

  public static void main(String[] args) throws IOException {
    GameProgress gameProgress = new GameProgress(94, 10, 2, 254.32);
    GameProgress gameProgress1 = new GameProgress(100, 2, 1, 125.36);
    GameProgress gameProgress2 = new GameProgress(23, 6, 6, 356.89);
    saveGame("D://Games/savegames/save.dat", gameProgress);
    saveGame("D://Games/savegames/save1.dat", gameProgress1);
    saveGame("D://Games/savegames/save2.dat", gameProgress2);
    zipFiles("D://Games/savegames/save.zip", "D://Games/savegames/");
    deleteIfNotZip("D://Games/savegames/");
    openZip("D://Games/savegames/save.zip", "D://Games/savegames/");
    openProgress("D://Games/savegames/packed_save1.dat");
  }
}
