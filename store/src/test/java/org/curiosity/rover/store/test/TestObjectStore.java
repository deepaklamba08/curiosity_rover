package org.curiosity.rover.store.test;

import com.fasterxml.jackson.databind.JsonNode;
import org.curiosity.rover.store.ObjectStore;
import org.curiosity.rover.store.QueryResultIterator;
import org.curiosity.rover.store.QueryStatement;
import org.curiosity.rover.store.filter.RelationalOperator;
import org.curiosity.rover.store.record.JsonRecord;
import org.curiosity.rover.store.record.Record;
import org.curiosity.rover.store.record.SimpleRecord;
import org.curiosity.rover.store.util.DataUtil;
import org.curiosity.rover.store.value.Values;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class TestObjectStore {

    private ObjectStore store;

    @BeforeSuite
    public void init() {
        this.store = new ObjectStore(TestHelper.getStoreBasePath());
    }

    @Test
    public void testRegisterObject() {
        this.store.registerObject("test_1", TestHelper.getObjectPath("test_data"), Collections.emptyMap());
    }

    @Test
    public void testSingleInsert() {
        QueryStatement statement = this.store.queryObject("test_1");
        statement.insertRecord(new SimpleRecord(Collections.singletonMap("name", Values.stringValue("abc"))));
    }

    @Test
    public void testBatchInsert() {
        Record data = new SimpleRecord(Collections.singletonMap("name", Values.stringValue("abc")));
        QueryStatement statement = this.store.queryObject("test_1");
        statement.batchInsertRecord(Arrays.asList(data, data, data, data, data));
    }

    @Test
    public void testInsertJsonData() throws IOException {
        JsonNode data = DataUtil.OBJECT_MAPPER.readTree(new File("D:\\dev\\curiosity_rover\\store\\src\\test\\resources\\data.json"));
        JsonRecord record = new JsonRecord(data);
        QueryStatement statement = this.store.queryObject("test_1");
        statement.insertRecord(record);
    }

    @Test
    public void testQuery() {
        QueryStatement statement = this.store.queryObject("test_1");
        QueryResultIterator result = statement.executeQuery(new RelationalOperator.Eq("name", Values.stringValue("abc")));
        while (result.hasNext()) {
            Record record = result.next();
            System.out.println(record);
        }
    }

    @Test
    public void testImportFile() {
        QueryStatement statement = this.store.queryObject("test_1");
        statement.importFile("E:\\work\\hawkins-lab\\stranger\\stranger-store\\src\\main\\resources\\repository\\json\\app_config.json");
    }
}
