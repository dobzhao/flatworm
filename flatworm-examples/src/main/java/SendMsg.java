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
@Record(name = "SendMsg", lines = {@Line}, converters = {
		@Converter(name = "date", clazz = CoreConverters.class, methodName = "convertDate", returnType = String.class), 
		@Converter(name = "char", clazz = CoreConverters.class, methodName = "convertChar", returnType = String.class), 
		@Converter(name = "bigDecimal", clazz = CoreConverters.class, methodName = "convertBigDecimal", returnType = String.class)})
public class SendMsg {

	@RecordElement(length = 10, converterName = "date", conversionOptions = @ConversionOption(name = "format", option = "yyyy-MM-dd"))
	private Date valueDate;
	
	@RecordElement(length = 16, converterName = "char", conversionOptions = {@ConversionOption(name = "justify", option = "left"), @ConversionOption(name = "pad-character", option = "M")})
	private String instrId;

	@RecordElement(length = 3, converterName = "char")
	private String currency;

	@RecordElement(length = 16, converterName = "bigDecimal", conversionOptions = {@ConversionOption(name = "decimal-implied", option = "true"), @ConversionOption(name = "decimal-places", option = "2"), @ConversionOption(name = "justify", option = "right")})  // 文本里以分为单位没有小数点，左对齐
	private BigDecimal amount;

}
