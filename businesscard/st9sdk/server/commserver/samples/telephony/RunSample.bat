:: ###########################################################################
:: #                                                                        
:: #  Telephony Adapter Sample Program                                        
:: #                                                                          
:: #    IBM   September 2007      
:: ###########################################################################

:: ###########################################################################
:: # Set Environment Variables                                                
:: ###########################################################################

:: ###########################################################################
:: # JAVA_HOME: Java 1.5 home directory                                       
:: #                                                                          
:: # For example: "C:\Program Files\Java\1.5\bin"                            
:: ###########################################################################
set JAVA_HOME=


:: ###########################################################################
:: # ADAPTER_JAR: the stcommsrvrtk.jar file path name                         
:: #                                                                          
:: # For example: "C:\Sametime\lib\stcommsrvrtk.jar"                          
:: ###########################################################################
set ADAPTER_JAR=


:: ###########################################################################
:: # ADAPTER_PROPERTIES_FILE_PATH: the path where 				 
:: #         	st.telephony.adapter.properties file is located		   
:: #                                                             	           
:: #                                                                          
:: # For example: "C:\Telephony"				                   
:: ###########################################################################
set ADAPTER_PROPERTIES_FILE_PATH=


:: ###########################################################################
:: # Activate the sample program                                             
:: #                                                                          
:: # Note: The sample class TelephonySample.class should be on the same       
:: #       directory as this batch file.                                     
:: ###########################################################################
%JAVA_HOME%\java -cp .;%ADAPTER_PROPERTIES_FILE_PATH%;%ADAPTER_JAR% TelephonySample



