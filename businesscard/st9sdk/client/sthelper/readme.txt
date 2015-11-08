
*******************************************************************************

         IBM(R) Sametime(R) 9.0 Software Development Kit
                      IBM Sametime Helper Toolkit

*******************************************************************************

This readme provides important information about the IBM Sametime Helper
Toolkit, which is part of the IBM Sametime Software Development Kit (SDK).

---------------------------------------
DOCUMENTATION ADDITIONS AND CORRECTIONS
---------------------------------------

IMPORTANT NOTE:

Use of the library with C# on early versions of Visual Studio may encounter a
Microsoft bug described in the Microsoft KnowledgeBase articles #913996 and 
918526.  The symptom is an error dialog when debugging which reports a 
"LoaderLock problem".  The articles state the issue is not encountered outside 
the debugger.

This debugger issue can be eliminated by using FixPack1 of Visual Studio, but 
note that programs created with FP1 may require an updated vc_redist on machines
not running Visual Studio.
