:: ###########################################################################
:: #                                                                         
:: #  CachedResolve Sample Program                                            
:: #                                                                          
:: #                                                IBM   January 2010        
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
:: # TOOLKIT_JAR: the stcommsrvrtk.jar file path name                         
:: #                                                                         
:: # For example: "C:\Sametime\lib\stcommsrvrtk.jar"                          
:: ###########################################################################
set TOOLKIT_JAR=


:: ###########################################################################
:: # CACHED_RESOLVER_PROPERTIES_FILE_PATH: the path where 			           
:: #         	st.cached.resolve.properties file  is located	               
:: #                                                             	           
:: #                                                                          
:: # For example: "C:\CachedResolve"				                           
:: ###########################################################################
set CACHED_RESOLVER_PROPERTIES_FILE_PATH=



:: ###########################################################################
:: # Activate the sample program                                              
:: #                                                                        
:: # Note: The sample class CachedResolveExample.class should be on the same  
:: #       directory as this batch file.                                      
:: ###########################################################################
%JAVA_HOME%\java -cp .;%CACHED_RESOLVER_PROPERTIES_FILE_PATH%;%TOOLKIT_JAR%  CachedResolveExample



