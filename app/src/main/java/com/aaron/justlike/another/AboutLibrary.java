package com.aaron.justlike.another;

public class AboutLibrary {

    private String mLibraryName;
    private String mLibraryAuthor;
    private String mLibraryDetails;

    public AboutLibrary(String libraryName, String libraryAuthor, String libraryDetails) {
        mLibraryName = libraryName;
        mLibraryAuthor = libraryAuthor;
        mLibraryDetails = libraryDetails;
    }

    public String getLibraryName() {
        return mLibraryName;
    }

    public void setLibraryName(String libraryName) {
        mLibraryName = libraryName;
    }

    public String getLibraryAuthor() {
        return mLibraryAuthor;
    }

    public void setLibraryAuthor(String libraryAuthor) {
        mLibraryAuthor = libraryAuthor;
    }

    public String getLibraryDetails() {
        return mLibraryDetails;
    }

    public void setLibraryDetails(String libraryDetails) {
        mLibraryDetails = libraryDetails;
    }
}
