package services;

import java.util.ArrayList;
import java.util.List;

public class Service_2_Impl implements Service_2 {
    @Override
    public List<String> work(String item) {
        List<String> list = new ArrayList<>();
        for(int i=0; i<10; i++) {
            list.add(item+" "+i);
        }
        return list;
    }
}
