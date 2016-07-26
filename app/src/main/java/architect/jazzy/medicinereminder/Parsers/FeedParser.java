package architect.jazzy.medicinereminder.Parsers;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import architect.jazzy.medicinereminder.Models.FeedItem;

/**
 * Created by Jibin_ism on 02-Jul-15.
 */
public class FeedParser {

    public static final String feedUrl = "http://www.medicinenet.com/rss/dailyhealth.xml";

    private static final String ns = null;

    /**
     * <channel>
     * <item>
     * <title>
     * Some Title
     * </title>
     * <link>
     * Some url
     * </link>
     * <pubDate>
     * Some Date
     * </pubDate>
     * <description>
     * Some html description
     * </description>
     * </item>
     * </channel>
     */

    public static final String TAG_ITEM = "item";
    public static final String TAG_TITLE = "title";
    public static final String TAG_LINK = "link";
    public static final String TAG_DESCRIPTION = "description";

    public static ArrayList<FeedItem> parse(){
        return parse(null);
    }

    public static ArrayList<FeedItem> parse(String response){

        ArrayList<FeedItem> feedItems = new ArrayList<>();
        try {

            Document document;

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            InputStream inputStream;
            if(response == null){
                File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/tmpMR");
                inputStream=new FileInputStream(new File(folder,"tmpMR00.tmp"));
                document = documentBuilder.parse(inputStream);
            }else{
                inputStream = new ByteArrayInputStream(response.getBytes());
                document = documentBuilder.parse(inputStream);
            }


            Element element=document.getDocumentElement();
            element.normalize();

            NodeList nodeList=document.getElementsByTagName("item");
            Log.e("FeedParser","Node Parsing");
            for(int i=0;i<nodeList.getLength();i++){
                Node node=nodeList.item(i);
                if(node.getNodeType()==Node.ELEMENT_NODE){
                    Element item=(Element)node;
//                    Log.e("FeedParser","Iterating through node: "+i+" "+getValue("title",item));
                    String s=getValue("pubDate",item);
                    Date date=new Date();
                    try {
                        DateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
                        date = (Date) format.parse(s);
//                        Log.e("Date",s+"     ===   "+DateFormat.getDateInstance().format(date));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    feedItems.add(new FeedItem(getValue("title",item), getValue("link",item),getValue("description",item),date));
                }
            }
        }catch (ParserConfigurationException pce){
            pce.printStackTrace();
        }catch (SAXException saxe){
            saxe.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

        return feedItems;
    }

    private static String getValue(String tag, Element element){
        NodeList nodeList=element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node=(Node)nodeList.item(0);
        return node.getNodeValue();
    }


//    public static ArrayList<FeedItem> parse(InputStream in) {
//        Log.e("FeedParser", "Parsing Started");
//        ArrayList<FeedItem> feedItems = new ArrayList<>();
//        try {
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setInput(in, "UTF-8");
//            int eventType = parser.getEventType();
//            Log.e("FeedParder", "Event Type " + eventType);
//            int i = 0;
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                Log.e("FeedParser", "Iterating");
//                String name = "";
//                switch (eventType) {
//                    case XmlPullParser.START_DOCUMENT:
//                        Log.e("FeedParser", "Start Document");
//                        i++;
//                        if (i > 2)
//                            return null;
//                        break;
//                    case XmlPullParser.START_TAG:
//                        name = parser.getName();
//                        Log.e("FeedParser parse", "Parsing start tag: " + name);
//                        switch (name) {
//                            case "item":
//                                FeedItem item = parseFeed(parser);
//                                if (item != null)
//                                    feedItems.add(item);
//                                break;
//                            default:
//                                break;
//                        }
//                        break;
//                    case XmlPullParser.END_TAG:
//                        Log.e("FeedParser", "End tag: " + parser.getName());
//                        if (parser.getName().equalsIgnoreCase("item"))
//                            break;
//                    default:
//                        break;
//                }
//                eventType = parser.next();
//            }
//            return feedItems;
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        } catch (IOException ie) {
//            ie.printStackTrace();
//        }
//        return feedItems;
//    }

    /*
    private static FeedItem parseFeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        int eventType = parser.getEventType();
        Log.e("FeedParser", "FeedParsing");
        String title = "", url = "", description = "";
        Date date=new Date();
        while (parser.getEventType() != XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase("item")) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
//                    Log.e("FeedParser", "in start tag of item");
                    switch (name) {
                        case "title":
                            title = parser.nextText();
//                            Log.e("FeedParser", "Title: " + title);
                            break;
                        case "link":
                            url = parser.nextText();
//                            Log.e("FeedParser", "Url: " + url);
                            break;
                        case "description":
                            description = parser.nextText();
//                            Log.e("FeedParser", "Description: " + description);
                            break;
                        case "pubDate":
                            String s=parser.nextText();
                            try {
                                DateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
                                date = (Date) format.parse(s);
                                Log.e("Date",s+"     ===   "+DateFormat.getDateInstance().format(date));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (!title.isEmpty()) {
                        return new FeedItem(title, url, description, date);
                    }
                    break;
                default:
                    break;
            }
            eventType = parser.next();
        }
        return null;
    }*/

//    public static ArrayList<FeedItem> parse(InputStream in) {
//        Log.e("FeedParser","Parsing Started");
//        ArrayList<FeedItem> feedItems = new ArrayList<>();
//        try {
//            XmlPullParser parser = Xml.newPullParser();
//            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//            parser.setInput(in, null);
//            parser.nextTag();
//            return readFeed(parser);
//        } catch (XmlPullParserException xe) {
//            xe.printStackTrace();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//
//        return feedItems;
//    }
//
//    private static ArrayList<FeedItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
//        Log.e("FeedParser","read Feed");
//
//        ArrayList<FeedItem> feedItems = new ArrayList<>();
//        parser.require(XmlPullParser.START_TAG, ns,"rss");
//
//        while(parser.next()!=XmlPullParser.END_TAG){
//            if(parser.getEventType()!=XmlPullParser.START_TAG){
//                continue;
//            }
//            String name=parser.getName();
//            if(name.equals("channel")){
//                return readChannel(parser);
//            }else{
//                skip(parser);
//            }
//        }
//
//        return feedItems;
//    }
//
//    private static ArrayList<FeedItem> readChannel(XmlPullParser parser) throws XmlPullParserException, IOException{
//        Log.e("FeedParser","read Feed");
//
//        ArrayList<FeedItem> feedItems = new ArrayList<>();
//        parser.require(XmlPullParser.START_TAG, ns,"channel");
//
//        while(parser.next()!=XmlPullParser.END_TAG){
//            if(parser.getEventType()!=XmlPullParser.START_TAG){
//                continue;
//            }
//            String name=parser.getName();
//            if(name.equals(TAG_ITEM)){
//                feedItems.add(readItem(parser));
//            }else{
//                skip(parser);
//            }
//        }
//
//        return feedItems;
//    }
//
//    private static FeedItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException{
//        Log.e("FeedParser","read Item");
//        parser.require(XmlPullParser.START_TAG,ns,TAG_ITEM);
//        String title=null, url=null, description=null;
//        while(parser.next()!=XmlPullParser.END_TAG){
//            if(parser.getEventType()!=XmlPullParser.START_TAG){
//                continue;
//            }
//            String name=parser.getName();
//            if(name.equals(TAG_TITLE)){
//                title=readTitle(parser);
//            }else if(name.equals(TAG_LINK)){
//                url=readUrl(parser);
//            }else if(name.equals(TAG_DESCRIPTION)){
//                description=readDescription(parser);
//            }
//            else{
//                skip(parser);
//            }
//        }
//        return new FeedItem(title, url, description);
//    }
//
//    private static String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException{
//        Log.e("FeedParser","read Title");
//        parser.require(XmlPullParser.START_TAG, ns, TAG_TITLE);
//        String title=readText(parser);
//        parser.require(XmlPullParser.END_TAG,ns, TAG_TITLE);
//        return title;
//    }
//
//    private static String readUrl(XmlPullParser parser) throws IOException, XmlPullParserException{
//        Log.e("FeedParser","read url");
//        parser.require(XmlPullParser.START_TAG, ns, TAG_LINK);
//        String link=readText(parser);
//        parser.require(XmlPullParser.END_TAG,ns, TAG_LINK);
//        return link;
//    }
//
//    private static String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException{
//        Log.e("FeedParser","read description");
//        parser.require(XmlPullParser.START_TAG, ns, TAG_DESCRIPTION);
//        String description=readText(parser);
//        parser.require(XmlPullParser.END_TAG,ns, TAG_DESCRIPTION);
//        return description;
//    }
//
//    private static String readText(XmlPullParser parser) throws XmlPullParserException, IOException{
//        Log.e("FeedParser","read Text");
//        String result="";
//        if(parser.next()==XmlPullParser.TEXT){
//            result=parser.getText();
//            parser.nextTag();
//        }
//        return result;
//    }
//
//    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException{
//        Log.e("FeedParser","skipping");
//        if(parser.getEventType()!=XmlPullParser.START_TAG){
//            Log.e("FeedParser","IllegalStateException");
//            return;
//        }
//        int depth=1;
//        while(depth!=0){
//            switch (parser.next()){
//                case XmlPullParser.END_TAG:
//                    depth--;
//                    break;
//                case XmlPullParser.START_TAG:
//                    depth++;
//                    break;
//            }
//        }
//
//    }
}
