package architect.jazzy.medicinereminder.Models;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jibin_ism on 19-Aug-15.
 */
public class SearchQuery {
    enum RetType{
        brief, topic, all;
    }
    private String term="",fileName="",server="";
    private int retstart=0,retmax=10;
    private RetType retType=RetType.brief;


    public SearchQuery() {
    }


    public static URL getSearchQueryURL(SearchQuery searchQuery) throws MalformedURLException{
        String url;
        if(searchQuery.getTerm().isEmpty() && searchQuery.getFileName().isEmpty()){
            throw new UnsupportedOperationException("No search Query or file");
        }
        if(searchQuery.getFileName().isEmpty()){
            url=WebSearch.getSearchURL(searchQuery.term);
        }else{
            url=WebSearch.getURLwithFile(searchQuery.getFileName(), searchQuery.getServer(), searchQuery.getRetstart());
        }

        url=WebSearch.getURLwithRetmax(url,searchQuery.getRetmax(),searchQuery.getRetType());
        url=WebSearch.appendRetStart(url,searchQuery.getRetstart());

        return new URL(url);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getRetstart() {
        return retstart;
    }

    public void setRetstart(int retstart) {
        this.retstart = retstart;
    }

    public int getRetmax() {
        return retmax;
    }

    public void setRetmax(int retmax) {
        this.retmax = retmax;
    }

    public RetType getRetType() {
        return retType;
    }

    public void setRetType(RetType retType) {
        this.retType = retType;
    }

    private static class WebSearch{
        private static final String baseURL="http://wsearch.nlm.nih.gov/ws/query";


        private static String getSearchURL(String term){
            String s=baseURL+"?db=healthTopics&term=";
            String termEscaped;
            String[] a=term.split(" ");
            if(a.length==1){
                termEscaped=a[0];
            }else{
                termEscaped="%20"+term.replace(" ","+")+"%20";
            }
            return s+termEscaped;
        }

        private static String getURLwithFile(String fileName,String server, int retstart){
            return baseURL+"?file="+fileName+"&server="+server+"&retstart"+retstart;
        }

        private static String getURLwithFileAndRetmax(String fileName,String server, int retstart, int retmax){
            return baseURL+"?file="+fileName+"&server="+server+"&retstart="+retstart+"&retmax="+retmax;
        }

        private static String getURLwithRetmax(String url,int retmax, RetType rettype){
            String s="";
            if(rettype==RetType.brief){
                s="brief";
            }else if(rettype==RetType.topic){
                s="topic";
            }else if(rettype==RetType.all){
                s="all";
            }
            return url+"&retmax="+retmax+"&rettype="+s;
        }

        private static String appendRetStart(String url, int retstart){
            return url+"&retstart="+retstart;
        }

    }
}
