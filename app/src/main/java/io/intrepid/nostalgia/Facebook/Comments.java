package io.intrepid.nostalgia.facebook;

public class Comments {
    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    String name;
    String comment;
    public Comments (String name, String comment){
        this.name = name;
        this.comment = comment;
    }
}
