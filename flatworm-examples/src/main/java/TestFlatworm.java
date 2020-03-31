import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;

import com.blackbear.flatworm.FileCreator;
import com.blackbear.flatworm.FileFormat;
import com.blackbear.flatworm.MatchedRecord;
import com.blackbear.flatworm.config.impl.DefaultAnnotationConfigurationReaderImpl;

public class TestFlatworm {

	public static void main(String[] args) {
		try (InputStream in = new ByteArrayInputStream("BD1234567890    CNY        9012348920200309".getBytes("UTF-8"))) {
			
			//receive
			DefaultAnnotationConfigurationReaderImpl configLoader = new DefaultAnnotationConfigurationReaderImpl();
            configLoader.setPerformValidation(true);
            FileFormat fileFormat = configLoader.loadConfiguration(ReceiveMsg.class);
//            fileFormat.setEnforceLineLengths(false);
            BufferedReader bufIn = new BufferedReader(new InputStreamReader(in));
            MatchedRecord matchedRecord = fileFormat.nextRecord(bufIn);
            Object bean = matchedRecord.getBean(ReceiveMsg.class.getName());
            ReceiveMsg msg = ReceiveMsg.class.cast(bean);
            System.out.println(msg.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //send
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()){
	        SendMsg sm = new SendMsg();
	        sm.setInstrId("TT12345");
	        sm.setCurrency("USD");
	        sm.setAmount(new BigDecimal(112233.8));
	        sm.setValueDate(new Date());
	        
	        FileCreator fc = new FileCreator(bos, SendMsg.class);
	        fc.setBean(SendMsg.class.getName(), sm);
	        fc.open();
	        fc.write(SendMsg.class.getName());
	        fc.close();
	        
	        System.out.println(new String(bos.toByteArray()));
	        
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
