package proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotations.Cache;

import static annotations.CacheType.*;

public class CacheProxy implements InvocationHandler {
    private Object o;
    private Map<Object[], Object> cahceMap = new HashMap<>(); // коллекция кешов (ключ - метод и значения его аргументов, значение - результат метода)

    public CacheProxy(Object o) {
        this.o = o;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        boolean annotationExists = false;
        for (Annotation ann : method.getAnnotations()) {
            if (ann.annotationType() == Cache.class) {
                annotationExists = true;
                break;
            }
        }
        if (!annotationExists)
            return method.invoke(o, args); // если метод не помечен аннотацией @Catche то возвращаем оригинал метода


        Cache cacheAnnotation = method.getAnnotation(Cache.class); // получаем аннотацию Cache с которой будет смотреть заданные параметры

        if (cacheAnnotation.cacheType() == IN_MEMORY) { // кеш возможно из памяти
            cahceMap.put(new Object[]{method, "tt", 10}, method.invoke(o,  "tt", 10));
            Object[] methodOfMap = findKeyParameter(method, args, cacheAnnotation.identityBy()); // проверяем по каким параметрам оценивать результат кеша (был ли такой кеш)

            if (methodOfMap == null) {
                result = method.invoke(o, args); // если в коллекции метода нет, то вызываем оригинальный метод
                result = getResult(method, result); // по пункту 3 (отрезаем результат если это List)
                cahceMap.put(new Object[]{method, args}, result);
            }
            else {
                result = cahceMap.get(new Object[]{method, args});
                System.out.println(result);
                result = getResult(method, result); // по пункту 3 (отрезаем результат если это List)
            }
        }
        System.out.println(result);

        if (cacheAnnotation.cacheType() == FILE) { // кеш возможно из файла
            String fileName;
            if (cacheAnnotation.fileNamePrefix().isEmpty())
                fileName = "C:\\Users\\ADN\\IdeaProjects\\JavaSchool_HW9_serialization\\src\\main\\resources\\" + method.getName() + ".bin";
            fileName = cacheAnnotation.fileNamePrefix(); // получаем путь к файлу

            // передаем файловому сериализатору данные

        }

        return null;
    }

    /**
     * метод  проверяет коллекцию кеша по методу и параметрам, изходя из фильтра (какие аргументы игнорировать при поиске кеша)
     */
    private Object[] findKeyParameter(Object method, Object[] args, int[] argsIgnore) {
        for (Object[] argMap : cahceMap.keySet()) { // цикл по ключам коллекции (где argMap[0] - имя метода
            if (!argMap[0].equals(method)) continue; // метод не совпадает
            boolean f = true;
            label_1:
            for (int i = 0; i < argMap.length - 1; i++) { //findKeyParameter цикл по значениям аргументов метода из кеша
                if (args.length > i) { // проверим значения текущего метода
                    for (int ignore : argsIgnore) {
                        if (ignore == i) continue label_1; // попускаем параметр (игнорируем)
                    }
                    String value_1 = args[i].toString();
                    String value_2 = argMap[i + 1].toString();
                    if (!value_1.equals(value_2)) {
                        f = false;
                        break label_1;
                    }
                }
            }
            if (f) { // метод с такими значениями аргументов найден
                System.out.println("Метод найден");
                return argMap;
            }
        }
        return null; // метод с такими аргументами в коллекции не найден
    }

    private Object getResult(Method method, Object result) throws Throwable{
        Class<?> returnType = method.getReturnType();
        // если метод возвращает список, вернуть только последние элементы. количество брать из аннотации
        if (returnType == List.class) {
            int listSize = method.getDeclaredAnnotation(Cache.class).listSize();
            if (listSize != -1) {
                List<?> listResult = (List)result;
                return listResult.subList(listResult.size() - listSize, listResult.size());
            }
        }
        return result;
    }

}
