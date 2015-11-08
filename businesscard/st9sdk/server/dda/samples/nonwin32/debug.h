/*
 * Licensed Materials - Property of IBM
 *
 * L-MCOS-96LQPJ
 *
 * (C) Copyright IBM Corp. 2002, 2013. All rights reserved.
 *
 * US Government Users Restricted Rights- Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */


#ifndef _DEBUG_INCLUDED_
#define _DEBUG_INCLUDED_

#include <stdio.h>
#include <assert.h>
#include "windows.h"

#ifdef _cpluscplus
extern "C" {
#endif

#define show(var)  _show(#var, var)
#define stringize(var) #var
#define dump(var, len)	_dump(stringize(var), ((char*)var), (len))

#ifdef _USE_DEBUG

# define	affirm(EX) debug._affirm((EX), #EX, __FILE__, __LINE__)

class Debug
{
private:
	char* Module;
	BOOL bShowEnter;
	FILE *Fp;
	pid_t PID;
public:
	Debug( const char* module, int show_enter = FALSE);
	~Debug();
public:
	FILE*  _openlog();
	void  _header(char* pLevel);
	void  vprint(const char* fmt, va_list ap);
	void  _print(const char* fmt, ...);
	void  _closelog();
	void print(const char* fmt, ...);
	void error(const char* fmt, ...);
	void warning(const char* fmt, ...);
	void trace(const char* fmt, ...);
	void notify(const char* fmt, ...);
	void before(const char* str);
	void after(const char* str);
	void _dump(char* bufname, char* buf, int buflen);
	void _trace(int lineno);
	void _show(const char* varname, int var);
	void _show(const char* varname, HANDLE var);
	void _show(const char* varname, const char* var);
	void failed(const char* str);
	void _affirm(int result, char* ex, char* file, int line);
	void checkfd(int fd);
	void countfds();
	void dumpfds();
};

#else  // ! _USE_DEBUG

# define	affirm(EX) assert((EX))

class Debug
{
public:
	Debug(const char* module) {;}
	Debug(const char* module, int show_enter) {;}
	void print(const char* fmt, ...) {;}
        void error(const char* fmt, ...) {;}
        void warning(const char* fmt, ...) {;}
        void trace(const char* fmt, ...) {;}
        void notify(const char* fmt, ...) {;}
	void before(const char* str) {;}
	void after(const char* str) {;}
	// void _dump(...) {;}
	void _dump(char* bufname, char* buf, int buflen) {;}
	void _trace(int lineno) {;}
	void _show(const char *, ...) {;}
	void failed(const char* str) {;}
	void _affirm(int result, char* ex, char* file, int line) {;}
	void checkfd(int fd) {;}
	void countfds() {;}
	void dumpfds() {;}
};

#endif // ! _USE_DEBUG

#ifdef _cpluscplus
}
#endif

#endif // _DEBUG_INCLUDED_
