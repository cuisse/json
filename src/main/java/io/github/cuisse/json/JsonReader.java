package io.github.cuisse.json;

public interface JsonReader {
    
    public char read();
    public boolean eof();
    public void dispose();
    
}
