/* ***************************************************************** */
/*                                                                   */
/* Licensed Materials - Property of IBM                              */
/*                                                                   */
/* L-MCOS-96LQPJ                                                     */
/*                                                                   */
/* (C) Copyright IBM Corp. 2002, 2013                                */
/*                                                                   */
/* US Government Users Restricted Rights- Use, duplication or        */
/* disclosure restricted by GSA ADP Schedule Contract with IBM Corp. */
/*                                                                   */
/* ***************************************************************** */

#ifndef __ST_THREAD_SAFE_h__
#define __ST_THREAD_SAFE_h__

#if defined(AIX) || defined(SOLARIS) || defined(LINUX) || defined(WIN32) || defined(OS400)
#define ST_THREAD_SAFE
#else
#undef  ST_THREAD_SAFE
#endif

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <time.h>



/*****************************************************************************
 * STTS guard
 ****************************************************************************/

#ifdef ST_THREAD_SAFE

#define STTS_GUARD()  __SttsGuard __stts_guard_##__LINE__

class __SttsGuard {
  // Functionality: Through constructor and destructor
public:
  __SttsGuard();
  ~__SttsGuard();

  // Blocked methods
private:
  __SttsGuard(const __SttsGuard& other);
  __SttsGuard& operator=(const __SttsGuard& other);

  // Members
private:
  int  m_locked;
};

#else  //ST_THREAD_SAFE

#define STTS_GUARD()  0

#endif //ST_THREAD_SAFE

/*****************************************************************************
 * STTS safe functions - inline
 ****************************************************************************/

inline char* sttsGetenv_r(
  const char*       _varname,
  char*             _out,
  size_t            _outsize
)
{
  STTS_GUARD();
  const char* val = getenv(_varname);
  if (!val) {
    return NULL;
  }
  
  size_t env_size = strlen(val);

  // Add 1 to the size for the NULL terminator
  if((env_size + 1) > _outsize)
    env_size = _outsize-1;
	
  strncpy(_out, val, env_size);
  _out[env_size]='\0';
  return _out;
}

// Similar to UNIX setenv / Windows SetEnvironmentVariable.
inline int sttsSetenv(const char* name, const char* value)
{
  STTS_GUARD();
#ifdef OS400
  char buffer[1024]="";
  sprintf(buffer, "%s=%s", name, value);
  return ::putenv(buffer);
#elif defined _UNIX
  return ::setenv(name, value, 1);
#else
  return ::_putenv_s(name, value);
#endif
}

inline struct tm* sttsLocaltime_r(
  const time_t*     _clock,
  struct tm*        _result
)
{
  STTS_GUARD();
#ifdef _UNIX
  return localtime_r(_clock, _result);
#else
  struct tm* val = localtime(_clock);
  if (!val) {
    return NULL;
  }
  *_result = *val;
  return _result;
#endif
}

#endif /* __ST_THREAD_SAFE_h__ */

