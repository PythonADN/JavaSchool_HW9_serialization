package proxy;

import java.io.File;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import annotations.Cache;
import serialization.MySerialization;

import static annotations.CacheType.*;

public class CacheProxy implements InvocationHandler {
    private Object o;
    private String rootPath;
    private Map<Object[], Object> cahceMap = new HashMap<>(); // коллекция кешов (ключ - метод и значения его аргументов, значение - результат метода)

    public CacheProxy(Object o, String rootPath) {
        this.o = o;
        this.rootPath = rootPath;
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
            Object[] methodOfMap = findKeyParameter(method, args, cacheAnnotation.identityBy()); // проверяем по каким параметрам оценивать результат кеша (был ли такой кеш)

            if (methodOfMap == null) {
                result = method.invoke(o, args); // если в коллекции метода нет, то вызываем оригинальный метод
                result = getResult(method, result); // по пункту 3 (отрезаем результат если это List и кладём в кеш)
                cahceMap.put(new Object[]{method, args}, result);
            } else {
                result = cahceMap.get(methodOfMap);
                //result = getResult(method, result); // по пункту 3 (отрезаем результат если это List)
            }
            return result;
        } else { // кеш возможно из файла: cacheAnnotation.cacheType() == FILE
            String fileName;
            if (cacheAnnotation.fileNamePrefix().isEmpty())
                fileName = rootPath + method.getName() + "_" + args + ".bin";
            fileName = cacheAnnotation.fileNamePrefix(); // получаем путь к файлу

            File cachedResultFile = Paths.get(rootPath, method.getName() + "_" + args + ".bin").toFile(); // был ли такой кеш в файле (есть ли такой файл)
            if (!cachedResultFile.exists()) {
                result = method.invoke(o, args); // если в файле метода нет, то вызываем оригинальный метод
                result = getResult(method, args); // по пункту 3 (отрезаем результат если это List и кладём в файл)
                MySerialization.serialize((Serializable) result, cachedResultFile, cacheAnnotation.zip()); // передаем файловому сериализатору данные
            }
            return MySerialization.deserialize(cachedResultFile, cacheAnnotation.zip()); // иначе дисериализуем
        }
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

    private Object getResult(Method method, Object result) throws Throwable {
        Class<?> returnType = method.getReturnType();
        // если метод возвращает список, вернуть только последние элементы. количество брать из аннотации
        if (returnType == List.class) {
            int listSize = method.getDeclaredAnnotation(Cache.class).listSize();
            if (listSize != -1) {
                List<?> listResult = (List) result;
                return listResult.subList(listResult.size() - listSize, listResult.size());
            }
        }
        return result;
    }

}
