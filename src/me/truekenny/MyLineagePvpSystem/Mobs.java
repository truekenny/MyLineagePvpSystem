package me.truekenny.MyLineagePvpSystem;

import org.bukkit.entity.LivingEntity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Mobs {
    final public static String FILENAME = "plugins/MyLineagePvpSystem/pvpmobs.data";
    private static Hashtable<Integer, MobData> mobDataHashtable = new Hashtable<Integer, MobData>();

    /**
     * Возвращает экземпляр сохранивший уровень существа
     *
     * @param entity
     * @return
     */
    public static MobData getMobData(LivingEntity entity) {

        Integer id = entity.getEntityId();

        MobData mobData = mobDataHashtable.get(id);
        if (mobData == null) {
            mobData = new MobData(entity);
            mobDataHashtable.put(id, mobData);
        }

        return mobData;
    }

    /**
     * Удаляет существо из списка (при смерти)
     *
     * @param id
     */
    public static void remove(Integer id) {
        mobDataHashtable.remove(id);
    }

    /**
     * Загрузка данных
     *
     * @return
     */
    public static boolean load() {
        BufferedReader br = null;

        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(FILENAME));

            while ((sCurrentLine = br.readLine()) != null) {
                System.out.println(sCurrentLine);

                StringTokenizer st = new StringTokenizer(sCurrentLine);
                String id = st.nextToken();
                String level = st.nextToken();

                MobData mobData = new MobData(level);
                mobDataHashtable.put(Integer.parseInt(id), mobData);

            }

        } catch (IOException e) {

        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return true;
    }

    /**
     * Сохранение данных
     *
     * @return
     */
    public static boolean save() {
        Enumeration<Integer> e = mobDataHashtable.keys();
        PrintWriter out;
        try {
            out = new PrintWriter(FILENAME);
        } catch (Exception ex) {
            return false;
        }

        while (e.hasMoreElements()) {
            Integer id = e.nextElement();
            MobData mobData = mobDataHashtable.get(id);

            out.printf("%d %d\n", id, mobData.level);
        }

        out.close();

        return true;
    }

}
