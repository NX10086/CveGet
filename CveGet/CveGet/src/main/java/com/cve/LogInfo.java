package com.cve;

public class LogInfo {
    String curFileName;
    int currentIndex;
    int pageCount;
    int startIndex;
    int totalResults;
    int resultsPerPage;
    String keyword;
    public LogInfo(){

    }
    public LogInfo(String curFileName, int currentIndex, int pageCount,int startIndex, int totalResults, int resultsPerPage, String keyword) {
        this.curFileName = curFileName;
        this.currentIndex = currentIndex;
        this.pageCount = pageCount;
        this.startIndex = startIndex;
        this.totalResults = totalResults;
        this.resultsPerPage = resultsPerPage;
        this.keyword = keyword;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getCurFileName() {
        return curFileName;
    }

    public void setCurFileName(String curFileName) {
        this.curFileName = curFileName;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int curentIndex) {
        this.currentIndex = curentIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
