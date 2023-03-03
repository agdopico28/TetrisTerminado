/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.ieselcaminas.tetris;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alu10191634
 */
public class Scores {
    public static final int NUM_HIGH_SCORES = 5;
    private List <Score>[] lists;
    
    
    
    public Scores(){
        lists = new ArrayList[3];
        for (int i = 0; i < lists.length ; i++){
            lists[i] = new ArrayList<>();
        }
        readDatFile();
        for(List<Score> list: lists){
            System.out.println(list);
        }
    }
    
    public void addScore(Score score){
        int level = score.getLevel();
        List <Score> list = lists [level];
        if(list.size() >= NUM_HIGH_SCORES){
            if (score.getScore() > getMinScore(level)){
                list.add(score);
                Collections.sort(list);
                list.remove(list.size() - 1);
            }
        } else{
            list.add(score);
            Collections.sort(list);
        }
        writeDatFile();
    }
    
    private int getMinScore(int level){
        int min = Integer.MAX_VALUE;
        for(Score s : lists[level]){
            if(s.getScore() < min){
                min = s.getScore();
            }
        }
        return min;
    }
    
    public void writeDatFile(){
        ObjectOutputStream output = null;
        try{
            output = new ObjectOutputStream(new FileOutputStream("scores.dat"));
            for(List<Score> list : lists){
                output.writeObject(list);
                
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(output != null){
                try{
                    output.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    private void readDatFile(){
        ObjectInputStream input = null;
        try{
            input = new ObjectInputStream(new FileInputStream("scores.dat"));
            int i = 0;
            while (true){
                lists[i]= (List<Score> )input.readObject();
                i++;
            }
        }catch(EOFException e){
            
        } catch (IOException ex) {
           ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            
        }finally{
            if(input != null){
                try{
                    input.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        
    }

    public List<Score>[] getLists() {
        return lists;
    }
    
    
}
