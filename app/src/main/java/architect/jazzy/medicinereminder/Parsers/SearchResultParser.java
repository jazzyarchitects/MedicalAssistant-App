package architect.jazzy.medicinereminder.Parsers;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import architect.jazzy.medicinereminder.HelperClasses.Constants;
import architect.jazzy.medicinereminder.Models.SearchResult;
import architect.jazzy.medicinereminder.Models.WebDocument;

/**
 * Created by Jibin_ism on 20-Aug-15.
 */
public class SearchResultParser {

    public static final String TAG = "SearchResultParser";
    private static XmlPullParserFactory xmlPullParserFactory;
    private static XmlPullParser xmlPullParser;

    public static SearchResult parse() {
        Log.e(TAG, "Parsing Started");

        SearchResult searchResult = new SearchResult();

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmpMR");


        ArrayList<WebDocument> searchDocument = new ArrayList<>();
        try {
            InputStream inputStream = new FileInputStream(new File(folder, Constants.SEARCH_FILE_NAME));

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document=documentBuilder.parse(inputStream);
            Element element=document.getDocumentElement();
            Log.e(TAG, document.toString());
            element.normalize();

            try{
                Node correctionNode=document.getElementsByTagName("spellingCorrection").item(0).getChildNodes().item(0);
                searchResult.setSpellingCorrection(correctionNode.getNodeValue());
            }catch (NullPointerException e){
                Log.i(TAG,"No spelling correction");
            }


            try {
                Node termNode = document.getElementsByTagName("term").item(0).getChildNodes().item(0);
                searchResult.setTerm(termNode.getNodeValue());
            }catch (NullPointerException e){
                Log.i(TAG, "No Search term");
            }

            try{
            Node fileNode=document.getElementsByTagName("file").item(0).getChildNodes().item(0);
            searchResult.setFile(fileNode.getNodeValue());
            }catch (NullPointerException e){
                Log.i(TAG, "No Search file");
            }

            try{
            Node serverNode=document.getElementsByTagName("server").item(0).getChildNodes().item(0);
            searchResult.setServer(serverNode.getNodeValue());
            }catch (NullPointerException e){
                Log.i(TAG, "No Server");
            }

            try{
            Node countNode=document.getElementsByTagName("count").item(0).getChildNodes().item(0);
                Log.e(TAG, "Count: " + countNode.getNodeValue());
            searchResult.setCount(Integer.parseInt(countNode.getNodeValue()));
            }catch (NullPointerException e){
                Log.i(TAG, "No count");
            }catch (NumberFormatException ne){
                Log.i(TAG, "Invalid count");
            }

            try{
            Node retstartNode=document.getElementsByTagName("retstart").item(0).getChildNodes().item(0);
            searchResult.setRetstart(Integer.parseInt(retstartNode.getNodeValue()));
                Log.e(TAG, "Retstart: " + retstartNode.getNodeValue());
            }catch (NullPointerException e){
                Log.i(TAG, "No Retstart");
            }catch (NumberFormatException ne){
                Log.i(TAG, "Invalid restart");
            }

            try{
            Node retmaxNode=document.getElementsByTagName("retmax").item(0).getChildNodes().item(0);
            searchResult.setRetmax(Integer.parseInt(retmaxNode.getNodeValue()));
                Log.e(TAG, "Retmax: " + String.valueOf(retmaxNode.getNodeValue()));
            }catch (NullPointerException e){
                Log.i(TAG, "No Retmax");
            }catch (NumberFormatException ne){
                Log.i(TAG, "Invalid Retmax");
            }


            NodeList nodeList=document.getElementsByTagName("document");

            Log.e(TAG,"Node Parsing");

            for(int i=0;i<nodeList.getLength();i++){
                Node node=nodeList.item(i);
                if(node.getNodeType()==Node.ELEMENT_NODE){
                    Element item=(Element)node;
                    searchDocument.add(getWebDocument(node));
                }
            }
            searchResult.setWebDocuments(searchDocument);
    } catch (Exception e) {
        e.printStackTrace();
    }

    return searchResult;


}


    static WebDocument parseDocument(XmlPullParser xmlPullParser) throws Exception {
        WebDocument webDocument = new WebDocument();

        String text = "";
        int eventType = xmlPullParser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = xmlPullParser.getName();
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    break;
                case XmlPullParser.TEXT:
                    text = xmlPullParser.getText().replaceAll("<span class=\".*\">", "").replaceAll("</span>", "");
                    break;
                case XmlPullParser.END_TAG:
                    switch (name) {
                        case "document":
                            webDocument.setUrl(xmlPullParser.getAttributeValue(null, "url"));
                            Log.e(TAG, "Document parsing Ending: url=" + webDocument.getUrl());
                            return webDocument;
                        case "content":
                            String attributeName = xmlPullParser.getAttributeValue(null, "name");
                            Log.e(TAG, "Document Parsing node: " + attributeName + " = " + text);
                            switch (attributeName) {
                                case "title":
                                    webDocument.setTitle(text);
                                    break;
                                case "organizationName":
                                    webDocument.setOrganizationName(text);
                                    break;
                                case "altTitle":
                                    webDocument.addAltTitle(text);
                                    break;
                                case "FullSummary":
                                    webDocument.setFullSummary(text);
                                    break;
                                case "mesh":
                                    webDocument.addMesh(text);
                                    break;
                                case "groupName":
                                    webDocument.addGroupName(text);
                                    break;
                                case "snippet":
                                    webDocument.setSnippet(text);
                                    break;
                                default:
                                    break;
                            }
                    }
                    break;
                default:
                    break;
            }
            eventType = xmlPullParser.next();
        }


        return webDocument;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

    private static WebDocument getWebDocument(Node node) {
        WebDocument webDocument = new WebDocument();

        Element element = (Element) node;
        webDocument.setUrl(element.getAttribute("url"));
//        Log.e(TAG,"Web Document url: "+element.getAttribute("url"));
        NodeList nodeList = element.getElementsByTagName("content");
//        Log.e(TAG,"Document Length: "+nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
//            Log.e(TAG,"NodeList ITEM: "+nodeList.item(i).getNodeValue());
//            Log.e(TAG,"NodeList ITEM1: "+nodeList.item(i).getChildNodes().getLength());
//            Log.e(TAG,"NodeList ITEM2: "+nodeList.item(i).getChildNodes().item(0).getNodeValue());
            Element element1 = (Element) nodeList.item(i);
//            Log.e(TAG,"ITEM i: "+element1.getTextContent());
//            Log.e(TAG,"ITEM 2: "+element1.getChildNodes().item(0).getNodeValue());
//            Log.e(TAG,"ITEM 2: "+element1.getChildNodes().item(1).getNodeValue());
//            Log.e(TAG,"Node: "+element1.getTagName()+element1.getNodeName()+element1.getSchemaTypeInfo().toString());
//            Log.e(TAG,"NodeValue: "+((Node)element1).getNodeValue());
            switch (element1.getAttribute("name")) {
                case "title":
                    webDocument.setTitle(element1.getTextContent().replaceAll("<span class=\".*\">", "").replaceAll("</span>", ""));
                    break;
                case "organizationName":
                    webDocument.setOrganizationName(element1.getTextContent().replaceAll("<span class=\".*\">", "").replaceAll("</span>", ""));
                    break;
                case "altTitle":
                    webDocument.addAltTitle(element1.getTextContent().replaceAll("<span class=\".*\">", "").replaceAll("</span>", ""));
                    break;
                case "FullSummary":
//                    Log.e(TAG,"Summary: "+element1.getTextContent());
                    webDocument.setFullSummary(element1.getTextContent());
                    break;
                case "mesh":
                    webDocument.addMesh(element1.getTextContent().replaceAll("<span class=\".*\">", "").replaceAll("</span>", ""));
                    break;
                case "groupName":
                    webDocument.addGroupName(element1.getTextContent().replaceAll("<span class=\".*\">", "").replaceAll("</span>", ""));
                    break;
                case "snippet":
                    webDocument.setSnippet(element1.getTextContent().replaceAll("<span class=\".*\">", "").replaceAll("</span>", ""));
                    break;
                default:
                    break;
            }
        }

        Log.e(TAG, webDocument.toString());
        return webDocument;
    }
}
