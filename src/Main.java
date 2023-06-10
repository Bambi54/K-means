import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {

        List<List<Double>> data = Files.lines(Paths.get("data/iris.data"))
                .map(e -> {
                    List<String> list = new ArrayList<>(List.of(e.split(",")));
                    list.remove(list.size()-1);
                    return list.stream().map(Double::parseDouble).collect(Collectors.toList());
                })
                .collect(Collectors.toList());

        Scanner s = new Scanner(System.in);
        System.out.println("Insert the amount of groups");
        int k = s.nextInt();

        kmeans(data, k);
    }

    public static void kmeans(List<List<Double>> data, int k){

        List<List<Double>> centroids = new ArrayList<>();

        List<List<Double>> data_copy = new ArrayList<>(data);

        for (int i = 0; i < k; i++) {
            Collections.shuffle(data_copy);
            centroids.add(data_copy.get(0));
            data_copy.remove(0);
        }

        HashMap<List<Double>, Integer> groups = new HashMap<>();

        for (int i = 0; i < 100; i++) {

            for (List<Double> elem : data) {

                List<Double> distances = new ArrayList<>();

                for (List<Double> centroid: centroids)
                    distances.add(distance(elem, centroid));

                double min = distances.stream().min(Double::compare).get();

                groups.put(elem, distances.indexOf(min));

            }

            List<List<Double>> new_centroids = new ArrayList<>();

            for (List<Double> centroid: centroids){

                int index = centroids.indexOf(centroid);

                List<List<Double>> keyList =
                        groups.entrySet().stream()
                                .filter(e -> e.getValue().equals(index))
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());

                if(keyList.size() > 0)
                    new_centroids.add(newCentroid(keyList));

            }

            if (centroids.equals(new_centroids))
                break;


            centroids = new_centroids;

            BigDecimal distsum = BigDecimal.valueOf(0);
            distsum = distsum.setScale(2,RoundingMode.HALF_UP);

            for (List<Double> centroid: centroids) {

                int index = centroids.indexOf(centroid);

                List<List<Double>> keyList =
                        groups.entrySet().stream()
                                .filter(e -> e.getValue().equals(index))
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());

                for (List<Double> elem : keyList)
                    distsum = distsum.add(BigDecimal.valueOf(distance(centroid, elem)));

            }

            System.out.println("Iteracja " + i + ": " + distsum);

        }

        for (int i = 0; i < centroids.size(); i++) {

            int finalI = i;

            System.out.println("Group: " + (i+1));

            groups.entrySet().stream()
                    .filter(e -> e.getValue() == finalI)
                    .forEach(e -> System.out.println(e.getKey()));


        }



    }

    public static double distance(List<Double> a, List<Double> b){

        double distance = 0;

        for (int i = 0; i < a.size(); i++)
            distance += Math.pow(a.get(i) - b.get(i), 2);


        BigDecimal bd = BigDecimal.valueOf(Math.pow(distance, 2));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();

    }

    public static List<Double> newCentroid(List<List<Double>> elems){

        List<Double> res = new ArrayList<>();

        for (int i = 0; i < elems.get(0).size(); i++) {

            double mean = 0;

            for (List<Double> elem : elems)
                mean += elem.get(i);
            
            BigDecimal bd = BigDecimal.valueOf(mean / elems.size());
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            res.add(bd.doubleValue());
        }

        return res;

    }
}
