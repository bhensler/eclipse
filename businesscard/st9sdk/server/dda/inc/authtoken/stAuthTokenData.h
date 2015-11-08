/*
 * Licensed Materials - Property of IBM
 *
 * L-MCOS-96LQPJ
 *
 * (C) Copyright IBM Corp. 2004, 2013. All rights reserved.
 *
 * US Government Users Restricted Rights- Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

#ifndef __STAUTHTOKENDATA_H__
#define __STAUTHTOKENDATA_H__

/* File: stAuthTokenData.h */

/*****************************************************************************
*
* ST Token Authentication Base Data 
*
*****************************************************************************/
 
typedef unsigned short TypeOfToken;
const TypeOfToken ST_SECTOKENFORMAT_UNKNOWN    = 0x0000;
const TypeOfToken ST_SECTOKENFORMAT_LTPATOKEN  = 0x0001;
const TypeOfToken ST_SECTOKENFORMAT_LTPATOKEN2 = 0x0002;

// Contains the token string and the token type
// Handles the memory allocation and deallocation, so the user doesn't has to worry about it
class TokenData {
  /* data members */
private:
  TypeOfToken m_type; 
  char        *m_value;
  /* constructors/destructor */
public:
  TokenData();
  TokenData(const char* value, TypeOfToken type);
  TokenData(const TokenData &other);
  ~TokenData();
  
  //assignment operator
  TokenData & operator=(const TokenData &other);

  //methods
  TypeOfToken getType() const;
  char* getValue() const;
  
  void setTypeAndValue(const TypeOfToken type, const char *value);
};

/* End of file stAuthTokenData.h */

#endif //__STAUTHTOKENDATA_H__
