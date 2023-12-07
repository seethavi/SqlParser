package com.paras.io;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scans a list of directories and identifies input files that meet specific
 * patterns.
 * 
 * @author seethavi
 */

public class DirectoryScanner {

    /*
     * Director to start the scanning from
     */
    private String startDir; // start directory for scanning
    /*
     * Set of file patterns to be included in the input set
     */
    private Set<Pattern> includeFilterSet;
    /*
     * Set of file patterns to be excluded from the input set
     */
    private Set<Pattern> excludeFilterSet;

    /**
     * Constructor
     * @param rootDir Directory to start scanning from 
     */
    public DirectoryScanner(String rootDir) {
        startDir = rootDir;
        includeFilterSet = new HashSet<Pattern>();
        excludeFilterSet = new HashSet<Pattern>();
    }

    public void addIncludeFilter(String filter) {
        includeFilterSet.add(Pattern.compile(filter));
    }
    
    public void addExcludeFilter(String filter) {
        excludeFilterSet.add(Pattern.compile(filter));
    }
    /**
     * Set 
     * @param filterList 
     */
    public void setIncludeFilterSet(String[] filterList) {
        if (filterList != null) {
            for (int i = 0; i < filterList.length; i++) {
                includeFilterSet.add(Pattern.compile(filterList[i]));
            }
        }
    }

    public void setExcludeFilterSet(String[] filterList) {
        if (filterList != null) {
            for (int i = 0; i < filterList.length; i++) {
                excludeFilterSet.add(Pattern.compile(filterList[i]));
            }
        }
    }

    public String getSource() {
        return startDir;
    }

    public Set<File> scan() {
        return listFiles(new File(startDir));

    }
    
    /**
     * Uses two filter lists to add files to the input set. By default the 
     * directory scanner adds the file to the set, if no include filters are present. Next, it
     * looks through the include pattern list. If the file matches, then it adds to the list. 
     * Finally, it goes through the
     * exclude pattern list. If the file matches the exclude pattern, then the file
     * will be explicitly removed. Please note that if the file matches a pattern in
     * the include list and the exclude list, then it will be excluded. The exclude
     * takes precedence over the include. If include needs to be given precedence, then
     * change the order of the application of the filters in the code below
     * 
     * @param source Root directory to start the recursive scan from
     * @return Returns a set of files identified for inclusion in the import process
     */

    private Set<File> listFiles(File source) {
        Set<File> fileSet = new HashSet<File>();
        if (source.isDirectory()) {
            File[] inputFiles = source.listFiles();
            if (inputFiles != null) {
                for (int i = 0; i < inputFiles.length; i++) {
                    String fileName = inputFiles[i].getPath();
                    
                    if (inputFiles[i].isFile() && (fileName.endsWith(".pkb") || fileName.endsWith(".prc")
                            || fileName.endsWith(".sql") || fileName.endsWith(".trg"))) {
                        if(includeFilterSet.isEmpty()) {
                            // filter further with specific names
                            fileSet.add(inputFiles[i]); // by default add the file if the include filter set is empty
                        }
                        else {
                            // apply filtering rule to validate if the file can be added
                            for(Iterator<Pattern> pi = includeFilterSet.iterator(); pi.hasNext();) {
                                Matcher matcher = pi.next().matcher(fileName);
                                if(matcher.matches()) {
                                    fileSet.add(inputFiles[i]);
                                    break;
                                }
                            }
                        }
                        
                        for(Iterator<Pattern> pi = excludeFilterSet.iterator(); pi.hasNext();) {
                            Matcher matcher = pi.next().matcher(fileName);
                            if(matcher.matches()) {
                                // remove the file from the list as this would have been
                                // added by default
                                fileSet.remove(inputFiles[i]);
                                break;
                            }
                        }
                    }
                    if (inputFiles[i].isDirectory()) {
                        Set<File> nestedFileSet = listFiles(inputFiles[i]); // recurse
                        fileSet.addAll(nestedFileSet);
                    } 
                }
            }
        }
        return fileSet;
    }
}
