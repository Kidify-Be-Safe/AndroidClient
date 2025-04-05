package com.antozstudios.myapplication.util;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FileHelper {


public static File directory;

Context context;

public boolean dataSaved;



    public static void writeData(Context context, String dir, String fileName, String profileName, int maxSpeed, boolean notification, boolean readNotification, ArrayList<String> data){
        directory = new File(context.getExternalFilesDir(null), dir);
        if(!dirExist(directory)){
            directory.mkdirs(); // Verzeichnis erstellen, falls es nicht existiert
        }

        File file = new File(directory, fileName+".txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(profileName + System.lineSeparator());
            outputStreamWriter.write(maxSpeed + System.lineSeparator());
            outputStreamWriter.write(String.valueOf(notification) + System.lineSeparator());
            outputStreamWriter.write(String.valueOf(readNotification) + System.lineSeparator());
            for(int i = 0; i < data.size(); i++){
                outputStreamWriter.write(data.get(i) + System.lineSeparator());
            }
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public static String getProfileFromProfile(Context context, String filename) {
        File file = new File(context.getExternalFilesDir(null) + "/App-Profiles/", filename + ".txt");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            stringBuilder.append(scanner.next());
            scanner.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return stringBuilder.toString();
    }

    public static String getSpeedFromProfile(Context context, String filename) {
        File file = new File(context.getExternalFilesDir(null) + "/App-Profiles/", filename + ".txt");
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Scanner scanner = new Scanner(file);
            String save ="";

           for(int i = 0;i<2;i++){
                save = scanner.next();
           }
            stringBuilder.append(save);
           scanner.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }



        return stringBuilder.toString();
    }



    public static ArrayList<String> getPackageNameOfProfile(Context context,String filename){
        File file = new File(context.getExternalFilesDir(null) + "/App-Profiles/", filename + ".txt");
        ArrayList<String> temp = new ArrayList<>();
        if(!file.exists()){
            return null;
        }
        int line = 0;
        try {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
                if (line < 4) {
                    scanner.nextLine(); // Überspringen Sie die ersten vier Zeilen
                } else {
                    temp.add(scanner.nextLine()); // Fügen Sie die Zeile zur Liste temp hinzu
                }
                line++;
            }

            scanner.close();


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        return temp;
    }


    public static boolean dirExist(File directory){
      return  directory.exists();
    }
}
