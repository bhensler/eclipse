
STHelperExampleps.dll: dlldata.obj STHelperExample_p.obj STHelperExample_i.obj
	link /dll /out:STHelperExampleps.dll /def:STHelperExampleps.def /entry:DllMain dlldata.obj STHelperExample_p.obj STHelperExample_i.obj \
		kernel32.lib rpcndr.lib rpcns4.lib rpcrt4.lib oleaut32.lib uuid.lib \
.c.obj:
	cl /c /Ox /DWIN32 /D_WIN32_WINNT=0x0400 /DREGISTER_PROXY_DLL \
		$<

clean:
	@del STHelperExampleps.dll
	@del STHelperExampleps.lib
	@del STHelperExampleps.exp
	@del dlldata.obj
	@del STHelperExample_p.obj
	@del STHelperExample_i.obj
