import java.math.BigDecimal;
import java.util.Date;

import com.blackbear.flatworm.annotations.ConversionOption;
import com.blackbear.flatworm.annotations.Converter;
import com.blackbear.flatworm.annotations.Line;
import com.blackbear.flatworm.annotations.Record;
import com.blackbear.flatworm.annotations.RecordElement;
import com.blackbear.flatworm.converters.CoreConverters;

import lombok.Data;

@Data
@Record(name = "ReceiveMsg", lines = {@Line}, converters = {
			@Converter(name = "date", clazz = CoreConverters.class, methodName = "convertDate", returnType = Date.class), 
			@Converter(name = "bigDecimal", clazz = CoreConverters.class, methodName = "convertBigDecimal", returnType = BigDecimal.class)
		})
public class ReceiveMsg {

	@RecordElement(length = 16)
	private String instrId;

	@RecordElement(length = 3)
	private String currency;

	@RecordElement(length = 16, converterName = "bigDecimal", conversionOptions = {@ConversionOption(name = "decimal-implied", option = "true"), @ConversionOption(name = "decimal-places", option = "2")})   // 文本里以分为单位没有小数点
	private BigDecimal amount; 

	@RecordElement(length = 8, converterName = "date", conversionOptions = @ConversionOption(name = "format", option = "yyyyMMdd"))
	private Date valueDate;

}
