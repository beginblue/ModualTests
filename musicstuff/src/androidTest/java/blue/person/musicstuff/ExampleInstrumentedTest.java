package blue.person.musicstuff;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import blue.person.music.Music;
import blue.person.musicstuff.DBStuff.DBController;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        DBController controller = new DBController(appContext);
        //controller.scanMusic();
        try {
            Thread.sleep(10000);
            List<Music> localMusic = controller.getMusicList("localMusic");
            System.out.println(localMusic.size());
        }catch (Exception e){
            e.getLocalizedMessage();

        }
//        DBOpenHelper helper = new DBOpenHelper(appContext,"blobtest",null,2);
//        SQLiteDatabase db = helper.getWritableDatabase();
//        db.execSQL("create table if not exists blobtest (name varchar(20),object blob);");
//        ContentValues values = new ContentValues();
//        values.put("name","test1");
//        Music music = new Music();
//        music.setTitle("test_test_test");
//        values.put("object", DBController.writeObject(music));
//        db.insert("blobtest",null,values);
//
//        Cursor blobtest = db.query("blobtest",
//                new String[]{"object"},
//                "name=?",
//                new String[]{"test1"},
//                null, null, null);
//        while(blobtest.moveToNext()){
//            byte[] blob = blobtest.getBlob(0);
//            Music musicRec = DBController.readObject(blob);
//            Log.i("blueBLOBTest", "useAppContext:"+ musicRec.getTitle());
//        }
        }
    }
