import com.google.common.base.Preconditions;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;

import static org.apache.spark.sql.functions.*;

public class EarthquakeSummaryTask {
    public static void main(String[] args) {
        Preconditions.checkArgument(args.length == 2, "Usage: EarthquakeSummaryTask <input_csv> <output_dir>");
        new EarthquakeSummaryTask().run(args[0], args[1]);
    }

    public void run(String inputCsvPath, String outputDirPath) {
        SparkConf conf = new SparkConf().setAppName(EarthquakeSummaryTask.class.getName()).setMaster("local[*]");

        JavaSparkContext jsc = new JavaSparkContext(conf);
        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();

        Dataset<Row> df = spark.read().option("header", "true")
                .option("inferSchema", "true")
                .option("delimiter",";")
                .csv(inputCsvPath);

        Dataset<Row> filtered = df.select(col("Disaster Type"), col("Magnitude"), col("Country")).filter(col("Disaster Type").equalTo("Earthquake")).filter(col("Magnitude").geq(6));

        Dataset<Row> summary = filtered.groupBy("Country").agg(count("*").alias("Num_Quakes"), avg("Magnitude").alias("Avg_Magnitude"), max("Magnitude").alias("Max_Magnitude"));

        summary.write().option("header", "true").mode(SaveMode.Overwrite).csv(outputDirPath);

        jsc.close();
    }
}
