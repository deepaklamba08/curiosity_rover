package org.curiosity.rover.store.test;

import com.fasterxml.jackson.databind.JsonNode;
import org.curiosity.rover.store.core.ObjectStore;
import org.curiosity.rover.store.core.QueryResultIterator;
import org.curiosity.rover.store.core.QueryStatement;
import org.curiosity.rover.store.filter.LogicalOperator;
import org.curiosity.rover.store.filter.Operator;
import org.curiosity.rover.store.filter.RelationalOperator;
import org.curiosity.rover.store.model.PartitionMetadata;
import org.curiosity.rover.store.record.JsonRecord;
import org.curiosity.rover.store.record.Record;
import org.curiosity.rover.store.record.MapRecord;
import org.curiosity.rover.store.util.DataUtil;
import org.curiosity.rover.store.value.Value;
import org.curiosity.rover.store.value.Values;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestObjectStore {

    private ObjectStore store;

    @BeforeSuite
    public void init() {
        this.store = new ObjectStore(TestHelper.getStoreBasePath());
    }

    @Test
    public void testRegisterObject() {
        PartitionMetadata p1 = new PartitionMetadata(0, "region");
        PartitionMetadata p2 = new PartitionMetadata(1, "type");

        Map<String, String> properties = new HashMap<>();
        properties.put("enableStats", "true");

        this.store.registerObject("asset", properties, Arrays.asList(p1, p2));
    }

    @Test
    public void testSingleInsert() {
        QueryStatement statement = this.store.queryObject("asset");
        statement.insertRecord(new MapRecord(Collections.singletonMap("name", Values.stringValue("abc"))));
    }

    @Test
    public void testBatchInsert() {
        Map<String, Value> recordData1 = new HashMap<>();
        recordData1.put("id", Values.stringValue("1"));
        recordData1.put("name", Values.stringValue("Data"));
        recordData1.put("region", Values.stringValue("us"));
        recordData1.put("type", Values.stringValue("file"));

        Map<String, Value> recordData2 = new HashMap<>();
        recordData2.put("id", Values.stringValue("2"));
        recordData2.put("name", Values.stringValue("text"));
        recordData2.put("region", Values.stringValue("uk"));
        recordData2.put("type", Values.stringValue("text"));

        Record data1 = new MapRecord(recordData1);
        Record data2 = new MapRecord(recordData2);

        QueryStatement statement = this.store.queryObject("asset");
        statement.batchInsertRecord(Arrays.asList(data1, data2, data1, data2, data1));
    }

    @Test
    public void testInsertJsonData() throws IOException {
        JsonNode data = DataUtil.OBJECT_MAPPER
                .readTree(new File("D:\\dev\\curiosity_rover\\store\\src\\test\\resources\\data.json"));
        JsonRecord record = new JsonRecord(data);
        QueryStatement statement = this.store.queryObject("test_1");
        statement.insertRecord(record);
    }

    @Test
    public void testQuery() {
        QueryStatement statement = this.store.queryObject("asset");
        Operator operator = new LogicalOperator.Or(
                new RelationalOperator.Eq("region", Values.stringValue("us")),
                new RelationalOperator.Eq("type", Values.stringValue("text")));
        QueryResultIterator result = statement.executeQuery(operator);
        while (result.hasNext()) {
            Record record = result.next();
            System.out.println(record);
        }
    }

    @Test
    public void testImportFile() {
        QueryStatement statement = this.store.queryObject("test_1");
        statement.importFile(
                "E:\\work\\hawkins-lab\\stranger\\stranger-store\\src\\main\\resources\\repository\\json\\app_config.json");
    }

    @Test
    public void testRollbackToVersion() {
        this.store.rollbackToVersion("asset", 1);
    }

    @Test
    public void testDeleteVersion() {
        this.store.deleteVersion("asset", 1);
    }

    @Test
    public void testRunCompaction() {
        this.store.queryObject("asset").runCompaction();
    }
}
