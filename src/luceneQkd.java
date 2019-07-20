/**
 * Created by Peilin on 7/16/19.
 */
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.store.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;


public class luceneQkd {

    public static final String CONTENT = "contents";
    public static final String filename = "./tables/D.csv";
    public static final String field = "dId";
    public static final String query = "20";
    public static final String FILE_PATH = "./index";
    public static final Path PATH = Paths.get(FILE_PATH);
    public static final int MAX_SEARCH = 10;


    public static void main(String[] args) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
        StandardAnalyzer analyzer = new StandardAnalyzer();
        // 1. create the index
        Directory index = new SimpleFSDirectory(PATH);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);

        // read D.csv
        String csvFile = filename;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        boolean flagFirstTime = true;

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                if(flagFirstTime){
                    flagFirstTime = false;
                    continue;
                }
                // use comma as separator
                String[] temp = line.split(cvsSplitBy);
                //System.out.println(temp[0]+" "+temp[1]+" "+temp[2]+" "+temp[3]);
                addD(w, temp[0].replaceAll("\"",""),
                        temp[1].replaceAll("\"",""),
                        temp[2].replaceAll("\"",""),
                        temp[3].replaceAll("\"",""));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        w.close();


        // the "title" arg specifies the default field to use
        // when no field is explicitly specified in the query.
        Query q = new QueryParser(field, analyzer).parse(query);

        // 3. search
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        // 4. display results
        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("url"));
        }

        // reader can only be closed when there
        // is no need to access the documents any more.
        reader.close();
        File file = new File("./index");
        FileUtils.cleanDirectory(file);
    }


    private static void addD(IndexWriter writerD, String dId, String len, String elen, String url) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("dId", dId, Field.Store.YES));
        doc.add(new TextField("len", len, Field.Store.YES));
        doc.add(new TextField("elen", elen, Field.Store.YES));
        doc.add(new TextField("url", url, Field.Store.YES));
        writerD.addDocument(doc);
    }

    private static void addKD(IndexWriter writerKD, String kId, String dId, String tf) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("kId", kId, Field.Store.YES));
        doc.add(new TextField("dId", dId, Field.Store.YES));
        doc.add(new TextField("tf", tf, Field.Store.YES));
        writerKD.addDocument(doc);
    }

    private static void addK(IndexWriter writerK, String kId, String value, String idf) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("kId", kId, Field.Store.YES));
        doc.add(new TextField("value", value, Field.Store.YES));
        doc.add(new TextField("idf", idf, Field.Store.YES));
        writerK.addDocument(doc);
    }
}