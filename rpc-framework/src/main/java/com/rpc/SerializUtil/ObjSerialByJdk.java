package com.rpc.SerializUtil;

import java.io.*;

/**
 * Demo class
 *
 * @author wanglei
 * @date create in 10:07 2018/7/9
 */
public class ObjSerialByJdk {

    public static byte[] convertToBytes(Object obj) {
        ByteArrayOutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            os = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.flush();
            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public static Object convertToObject(byte[] bytes) {
        InputStream is = null;
        ObjectInputStream ois = null;
        try {
            is = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(is);
            Object obj = ois.readObject();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }
}
