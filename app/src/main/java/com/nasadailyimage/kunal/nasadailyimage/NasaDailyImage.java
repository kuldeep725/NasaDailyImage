package com.nasadailyimage.kunal.nasadailyimage;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NasaDailyImage extends AppCompatActivity {
    IotdHandler handler = new IotdHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            setContentView(R.layout.small_screen);
        }
        else {
            setContentView(R.layout.activity_nasa_daily_image);
        }
        //handler = new IotdHandler ();
        new MyTask().execute();
    }

    public void onRefresh(View view) {
        refreshFromFeed();
    }
    private void refreshFromFeed() {
        handler.processFeed();
        resetDisplay(handler.getTitle(), handler.getDate(), handler.getImage(), handler.getDescription());
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            setContentView(R.layout.landscape);
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_nasa_daily_image);
        }
    }

    public class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            handler.processFeed();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            resetDisplay (handler.getTitle(), handler.getDate(), handler.getImage(), handler.getDescription());
            super.onPostExecute(result);
        }
    }
    private void resetDisplay(String title, String date, Bitmap image, StringBuffer description) {
        TextView titleView = (TextView) findViewById (R.id.image_title);
        titleView.setText(title);

        TextView dateView = (TextView) findViewById(R.id.pubDate);
        dateView.setText(date);

        ImageView imageView = (ImageView) findViewById (R.id.imageView);
        imageView.setImageBitmap(image);

        TextView descriptionView = (TextView) findViewById (R.id.imageDescription);
        descriptionView.setText(description);
    }

    public class IotdHandler extends DefaultHandler {
        private String url = "http://www.nasa.gov/rss/dyn/image_of_the_day.rss";
        private boolean inUrl = false;
        private boolean inTitle = false;
        private boolean inDescription = false;
        private boolean inItem = false;
        private boolean inDate = false;
        private Bitmap image = null;
        private String title = null;
        private StringBuffer description = new StringBuffer();
        private String date = null;


        public void processFeed() {
            try {
                SAXParserFactory factory =
                        SAXParserFactory.newInstance();
                SAXParser parser = factory.newSAXParser();
                XMLReader reader = parser.getXMLReader();
                reader.setContentHandler(this);
                InputStream inputStream = new URL(url).openStream();
                reader.parse(new InputSource(inputStream));
            } catch (Exception e) {  }
        }





        public Bitmap getBitmap(String url) {
            try {
                HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bilde = BitmapFactory.decodeStream(input);
                input.close();
                return bilde;
            } catch (IOException ioe) { return null; }
        }

        public void startElement(String url, String localName, String qName, Attributes attributes) throws SAXException {
            if (localName.endsWith(".jpg")) { inUrl = true; }
            else { inUrl = false; }

            if (localName.startsWith("item")) { inItem = true; }
            else if (inItem) {

                if (localName.equals("title")) { inTitle = true; }
                else { inTitle = false; }

                if (localName.equals("description")) { inDescription = true; }
                else { inDescription = false; }

                if (localName.equals("pubDate")) { inDate = true; }
                else { inDate = false; }
            }
        }


        public void characters(char ch[], int start, int length) { String chars = new String(ch).substring(start, start + length);
            if (inUrl && url == null) { image = getBitmap(chars); }
            if (inTitle && title == null) { title = chars; }
            if (inDescription) { description.append(chars); }
            if (inDate && date == null) { date = chars; }
        }

        public Bitmap getImage() { return image; }
        public String getTitle() { return title; }
        public StringBuffer getDescription() { return description; }
        public String getDate() { return date; }


    }
   /* public class NasaIotd extends Activity {
        ProgressDialog dialog;
        Bitmap image;
        IotdHandler handler = new IotdHandler();
        handler.processFeed();
        image = getBitmap(handler.getImage());
    }*/
    public void onSetWallpaper(View view) {/*
        Thread th = new Thread();

        public void run() {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(NasaIotd.this);
        }*/
    }


}
