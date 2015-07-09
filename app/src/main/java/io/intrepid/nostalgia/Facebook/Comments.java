package io.intrepid.nostalgia.facebook;

public class Comments {
    String name;
    String comment;
    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public Comments (String name, String comment){
        this.name = name;
        this.comment = comment;
    }
}
