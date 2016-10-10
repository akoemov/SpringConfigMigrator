package com.akoemov;

import java.io.File;

/**
 * Created by Alexander Akoemov on 10/10/2016.
 */
public class Migrator {

    public static void main(String[] args) {
        File configFile = new Finder().getXMLConfig();
        String javaConfig = new ParserXML().parsXMLFile( configFile);
        System.out.println(javaConfig);
    }
}
