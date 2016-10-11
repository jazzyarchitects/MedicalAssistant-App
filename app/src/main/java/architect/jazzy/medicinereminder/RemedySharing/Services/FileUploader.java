package architect.jazzy.medicinereminder.RemedySharing.Services;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Jibin_ism on 20-Dec-15.
 */
public class FileUploader extends AsyncTask<Void, Void, String> {

  URL mUrl;
  String method;
  File file;
  FileUploadListener fileUploadListener;
  ResultListener resultListener;
  ErrorListener errorListener;

  public FileUploader(String url, File file) {
    this(url, "POST", file);
  }

  public FileUploader(String url, String method, File file) {
    try {
      this.mUrl = new URL(url);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    this.file = file;
    this.method = method;
    if (!method.equalsIgnoreCase("PUT") && !method.equalsIgnoreCase("POST") && !method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("DELETE")) {
      throw new RuntimeException("Unexpected Backend interface method: " + method);
    }
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    if (fileUploadListener != null) {
      fileUploadListener.onPreExecute();
    }
  }

  @Override
  protected String doInBackground(Void... voids) {

    HttpURLConnection urlConnection = null;
    try {
      urlConnection = (HttpURLConnection) mUrl.openConnection();
      urlConnection.setRequestMethod(method);
      urlConnection.setConnectTimeout(30000);
      urlConnection.setReadTimeout(45000);
      urlConnection.setRequestProperty("Content-Type", "multipart/form-data");
      urlConnection.setRequestProperty("Connection", "Keep-Alive");
      urlConnection.setDoInput(true);
      urlConnection.setDoOutput(true);


      urlConnection.addRequestProperty("x-service-id", "androidApp1958-2013JE0305");

      urlConnection.connect();


      OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());

      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bfis = new BufferedInputStream(fis);
      byte[] buffer = new byte[1024];
      int bufferLength = 0;

      while ((bufferLength = bfis.read(buffer)) > 0) {
        os.write(buffer, 0, bufferLength);
      }

      os.flush();
      os.close();


      BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
      String s = "";
      String line;
      while ((line = reader.readLine()) != null) {
        s += line;
      }
      urlConnection.disconnect();
      return s;
    } catch (IOException e) {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      e.printStackTrace();
      if (errorListener != null) {
        errorListener.onError(e);
      }
      if (fileUploadListener != null) {
        fileUploadListener.onError(e);
      }
    }
    return null;
  }

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);
    if (fileUploadListener == null && resultListener == null) {
      throw new RuntimeException("Result not passed to calling function...");
    }
    if (resultListener != null) {
      resultListener.onResult(s);
    }
    if (fileUploadListener != null) {
      fileUploadListener.onResult(s);
    }
  }

  public void setFileUploadListener(FileUploadListener fileUploadListener) {
    this.fileUploadListener = fileUploadListener;
  }

  public void setResultListener(ResultListener resultListener) {
    this.resultListener = resultListener;
  }

  public void setErrorListener(ErrorListener errorListener) {
    this.errorListener = errorListener;
  }

  public interface FileUploadListener {
    void onPreExecute();

    void onError(Exception e);

    void onResult(String result);
  }

  public interface ResultListener {
    void onResult(String result);
  }

  public interface ErrorListener {
    void onError(Exception e);
  }
}
