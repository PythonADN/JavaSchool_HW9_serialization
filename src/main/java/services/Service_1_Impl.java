package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Service_1_Impl implements Service_1 {
    @Override
    public List<String> run(String item, int value) {
        List<String> list = new ArrayList<>();
        for(int i=0; i<value; i++) {
            list.add(item+" "+value);
        }
        return list;
    }
}
