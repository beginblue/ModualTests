package blue.person.internetstuff;

import org.ow2.util.base64.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 酷狗搜索歌词API
 * http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=歌曲名&duration=歌曲总时长(毫秒)&hash=歌曲Hash值
 * <p>
 * 例如http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=Impossible&duration=228022&hash=
 * 返回的是这样的：
 * {
 * "accesskey":"27B55E2D29634DBD6A49D39CB83338A7",
 * "adjust":0,
 * "duration":224156,
 * "id":"16230032",
 * "score":10,
 * "singer":"",
 * "song":"impossible",
 * "uid":"618122938"
 * }
 * ],
 * "info":"OK",
 * "keyword":"Impossible",
 * "proposal":"13182315",
 * "status":200
 * }
 * <p>
 * <p>
 * 酷狗下载歌词API(红色参数通过搜索得到)
 * http://lyrics.kugou.com/download?
 * ver=1&
 * client=pc&
 * id=10515303&
 * accesskey=3A20F6A1933DE370EBA0187297F5477D&
 * fmt=lrc&
 * charset=utf8 （fmt=lrc 或 krc）
 * <p>
 * 因为搜索必须要带上歌曲总时长，不然搜不出，Hash可以不需要。
 * <p>
 * Created by getbl on 2017/5/5.
 */

public class LRCRequests {

    /**
     * 搜索可用的歌词
     * http://lyrics.kugou.com/search
     * ?
     * ver=1&
     * man=yes&
     * client=pc&
     * keyword=歌曲名&
     * duration=歌曲总时长(毫秒)&
     * hash=歌曲Hash值
     */
    public RequestListEntity requestList(String name, long duration) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)
                new URL("http://lyrics.kugou.com/search?ver=1&man=yes&client=pc&keyword=" + name + "&duration=" + duration + "&hash=")
                        .openConnection();
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String str = reader.readLine();
        String res = "";
        while (str != null) {
            res += str;
            str = reader.readLine();
        }
        // System.out.println(res);
        return RequestListEntity.objectFromData(res);

    }

    public String getLRCs(String accessKey, String id) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection)
                    new URL("http://lyrics.kugou.com/download?ver=1&client=pc&id=" + id + "&accesskey=" + accessKey + "&fmt=lrc&charset=utf8")
                            .openConnection();
            connection.setConnectTimeout(5000);
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String str = reader.readLine();
            String res = "";
            while (str != null) {
                res += str;
                str = reader.readLine();
            }
            //System.out.println(res);
            LRCEntity entity = LRCEntity.objectFromData(res);
            byte[] decode = Base64.decode(entity.getContent().toCharArray());
            return new String(decode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
