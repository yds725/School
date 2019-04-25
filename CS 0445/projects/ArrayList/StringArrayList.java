
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class StringArrayList implements StringList {
    
    private String[] originalArray;
    private String[] copiedArray;
    private int counter;
    
    public StringArrayList(){
        counter = 0;
        originalArray = new String[0];
    }
    
    public int add(String s){      
        counter++;
        copiedArray = Arrays.copyOf(originalArray, counter);
        copiedArray[counter-1] = s;
        originalArray = copiedArray;
        
        int index = counter - 1;
        return index;
    }
    
    public String get(int index){
        if (index >= counter) throw new IndexOutOfBoundsException();
        return originalArray[index];
    }
    
    public boolean contains(String s){
        return indexOf(s) >= 0;
    }
    
    public int indexOf(String s){
        int index = -1;
        for(int i = 0; i < originalArray.length; i++){
            if(originalArray[i]==s){
                index = i;
                break;
            }
        }
        
        return index;
    }
    
    public int size(){
        return counter;
    }
    
    public int add(int index, String s){
       // if(index >= counter) throw new IndexOutOfBoundsException();
        String[] clonedArray;
        counter++;
        clonedArray = Arrays.copyOf(originalArray, counter);
        clonedArray[index] = s;
        for(int i = index; i < originalArray.length; i++){
            clonedArray[i+1] = originalArray[i];
        }
        originalArray = clonedArray;
        
        return index;
    }
    
    public void clear(){
        counter = 0;
        originalArray = new String[counter];
    }
    
    public boolean isEmpty(){
        return counter == 0;
    }
    
    public String remove(int index){
        if(counter == 0) throw new IndexOutOfBoundsException();
        String[] clonedArray;
        String removedElement;
        removedElement = originalArray[index];
        counter--;
        clonedArray = Arrays.copyOf(originalArray, counter);
        for(int i = index; i < originalArray.length - 1; i++){
            clonedArray[i] = originalArray[i+1];
        }
        originalArray = clonedArray;
        
        return removedElement;
    }
    
    public void set(int index, String s)  {
        if(index >= counter) throw new IndexOutOfBoundsException();
        originalArray[index] = s;
    }
    
    public String[] toArray(){
        String[] clone = new String[counter];
        System.arraycopy(originalArray, 0, clone, 0, counter);
        
        return clone;
    }
}
