package blue.person.music;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 音乐类
 * Created by getbl on 2017/1/11.
 */

public class Music
        implements  Serializable {

    private static final long serialVersionUID = 314159265354L;

    private long id;
    private String title;//标题
    private String artist;//艺术家
    private String album;//专辑
    private long duration;//时长
    private String uri;//音乐uri
    private long albumId;//封面专辑id 根据id拿到专辑图片
    private String coverUri;//
    private String fileName;//文件名
    private long fileSize;//文件大小
    private String year;//年份
    private String comment;//评论备注

    public Music(long id, String title, String artist, String album, long duration, String uri, long albumId, String coverUri, String fileName, long fileSize, String year) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.uri = uri;
        this.albumId = albumId;
        this.coverUri = coverUri;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.year = year;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }


    public Music() {
    }




    /**
     * 2017.3.7
     * 解构mp3文件格式
     * 从文件中获取歌曲信息
     */


    private Bitmap cover; //封面
    private String mFilePath; //文件位置
    private Map<String,byte[]> ID3Data;


    public Music(String filePath) throws Exception {
        if(!filePath.endsWith("mp3")){
            throw new Exception("文件格式必须是MP3");
        }
        mFilePath = filePath;
        setUri(mFilePath);
        readBlocks();
        progressDetails();
    }



    /**
     * 读取MP3文件中的ID3块
     *
     * @throws IOException 文件找不到
     */
    public void readBlocks() throws IOException {
        ID3Data = new HashMap<>();
        byte[] bytes = new byte[4];
        FileInputStream fileStream = new FileInputStream(new File(mFilePath));//文件转为流
        fileStream.read(bytes);
        String title = new String(bytes);
        if (title.contains("ID3") && bytes[3] == 3) {
            //副版本号
            fileStream.read();
            //flag
            fileStream.read();

            byte[] bSize = new byte[4];
            fileStream.read(bSize);
            long size = 0;
            fileStream.read(bytes);
            while (bytes[0] != (byte) 0xff && bytesToLong2(bytes) != 0) {
                title = new String(bytes);
                fileStream.read(bSize);
                size = bytesToLong2(bSize);
                //flag 2bytes
                fileStream.read();
                fileStream.read();
                byte[] content = new byte[(int) size];
                fileStream.read(content);

                ID3Data.put(title, content);

                fileStream.read(bytes);
            }
        }
    }

    private String readStr(byte[] contains) throws UnsupportedEncodingException {
        int code = contains[0];
        String HexContains;
        String charset = "UTF-8";
        switch (code) {
            case 0:
                charset = "ISO-8859-1";
                break;
            case 1:
                charset = "UTF-16";
                break;
            case 2:
                charset = "UTF-16BE";
                break;
            default:
                break;
        }
        HexContains = new String(contains, 1, contains.length - 1, charset);
        return HexContains;
    }


    /**
     * 将属性赋值到对象属性中
     * TRCK:1 //track number
     * COMM:XXX 163 key(Don't modify): //comment
     * TALB:灯火山2017 //album
     * TIT2:伤心的人别听慢歌（Cover：五月天） //title/songName
     * TSSE:Lavf57.25.100 //software/hardware and settings uesd for encoding
     * TPE1:Mt.Ember // lead performer
     * APIC //图片文件的字节
     */
    public void progressDetails() throws UnsupportedEncodingException {
        for (String str :
                ID3Data.keySet()) {
            byte[] data = ID3Data.get(str);
           switch (str){
               case "TRCK":
                   this.setAlbumId(Integer.valueOf(readStr(data)));
                   break;
               case "COMM":
                   this.setComment(readStr(data));
                   break;
               case "TALB":
                   this.setAlbum(readStr(data));
                   break;
               case "TIT2":
                   this.setTitle(readStr(data));
                   break;
               case "TPE1":
                   this.setArtist(readStr(data));
                   break;
               case "APIC":
                  // byte[] btmap =  new byte[data.length-13];
                 //  System.arraycopy(data,13,btmap,0,data.length-13);
                  // Log.e(mFilePath, "progressDetails: "+Bytes2HexString(btmap) );
                   Bitmap bitmap = BitmapFactory.decodeByteArray(data,13,data.length-13);
                   this.setCover(bitmap);
                   break;
               default:
                   break;

            }
        }
    }

    private String Bytes2HexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private Bitmap decodeBitmap(byte[] data){

        return null;
    }

    private long bytesToLong(byte[] b) {
        if (b.length != 4) return -1;
        long res = 0;
        res += b[3] & 0x7f;
        res += (b[2] & 0x7f) << 7;
        res += (b[1] & 0x7f) << 14;
        res += (long) (b[0] & 0xff) << 21;
        return res;
    }

    private long bytesToLong2(byte[] b) {
        if (b.length != 4) return -1;
        long res = 0;
        res += b[3] & 0xff;
        res += (b[2] & 0xff) << 8;
        res += (b[1] & 0xff) << 16;
        res += (long) (b[0] & 0xff) << 24;
        return res;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Bitmap getCover() {
        return this.cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }
}
