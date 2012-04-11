/*************************************************************************
Copyright (C) 2011  CERTAE  Universite Laval  
Dario Gomez 
 **********************************************************************/

package org.certae.plugins.export.mrd.wrappers;

public class StringWrapper {
    private String s;

    public StringWrapper(String s) {
        this.s = (s == null) ? "" : s;
    }

    @Override
    public String toString() {
        return s;
    }

    //convert "camel_case" to "camelCase"
    public StringWrapper getCamelCase() {

    	String result = "";
        boolean nextUpper = false;
        String s0 = toISO(this.s); 
        
        for (int i = 0; i < s0.length(); i++) {
            Character currentChar = s0.charAt(i);
            if (currentChar == '_' || currentChar == ' ' || currentChar == '-' || currentChar == '\'') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    currentChar = Character.toUpperCase(currentChar);
                    nextUpper = false;
                }
                result = result.concat(currentChar.toString());
            }
        }

        return new StringWrapper(result);
    }

    public StringWrapper getCapitalized() {

    	String capitalized = getCamelCase().toString();

        if ((this.s != null) && (this.s.length() > 1)) {
            capitalized = Character.toUpperCase(capitalized.charAt(0)) + capitalized.substring(1);
        } //end if

        return new StringWrapper(capitalized);
    }

    public StringWrapper getUncapitalized() {
        String uncapitalized = "";

        if ((this.s != null) && (this.s.length() > 1)) {
            uncapitalized = Character.toLowerCase(this.s.charAt(0)) + this.s.substring(1);
        } //end if
        return new StringWrapper(uncapitalized);
    }

    public String  toISO() {
    	String s = toISO(this.s);
    	return s;
    }

    private String  toISO( String s0) {  

    	s0=charToIso(s0,156,"oe");
    	s0=charToIso(s0,158,"z");
    	s0=charToIso(s0,159,"y");
    	s0=charToIso(s0,161,"i");
    	s0=charToIso(s0,162,"c");
    	s0=charToIso(s0,165,"y");
    	s0=charToIso(s0,181,"m");
    	s0=charToIso(s0,192,"A");
    	s0=charToIso(s0,193,"A");
    	s0=charToIso(s0,194,"A");
    	s0=charToIso(s0,195,"A");
    	s0=charToIso(s0,196,"A");
    	s0=charToIso(s0,197,"A");
    	s0=charToIso(s0,198,"AE");
    	s0=charToIso(s0,199,"C");
    	s0=charToIso(s0,200,"E");
    	s0=charToIso(s0,201,"E");
    	s0=charToIso(s0,202,"E");
    	s0=charToIso(s0,203,"E");
    	s0=charToIso(s0,204,"I");
    	s0=charToIso(s0,205,"I");
    	s0=charToIso(s0,206,"I");
    	s0=charToIso(s0,207,"I");
    	s0=charToIso(s0,208,"D");
    	s0=charToIso(s0,209,"N");
    	s0=charToIso(s0,210,"O");
    	s0=charToIso(s0,211,"O");
    	s0=charToIso(s0,212,"O");
    	s0=charToIso(s0,213,"O");
    	s0=charToIso(s0,214,"O");
    	s0=charToIso(s0,215,"x");
    	s0=charToIso(s0,216,"0");
    	s0=charToIso(s0,217,"U");
    	s0=charToIso(s0,218,"U");
    	s0=charToIso(s0,219,"U");
    	s0=charToIso(s0,220,"U");
    	s0=charToIso(s0,221,"Y");
    	s0=charToIso(s0,223,"B");
    	s0=charToIso(s0,224,"a");
    	s0=charToIso(s0,225,"a");
    	s0=charToIso(s0,226,"a");
    	s0=charToIso(s0,227,"a");
    	s0=charToIso(s0,228,"a");
    	s0=charToIso(s0,229,"a");
    	s0=charToIso(s0,230,"ae");
    	s0=charToIso(s0,231,"c");
    	s0=charToIso(s0,232,"e");
    	s0=charToIso(s0,233,"e");
    	s0=charToIso(s0,234,"e");
    	s0=charToIso(s0,235,"e");
    	s0=charToIso(s0,236,"i");
    	s0=charToIso(s0,237,"i");
    	s0=charToIso(s0,238,"i");
    	s0=charToIso(s0,239,"i");
    	s0=charToIso(s0,240,"d");
    	s0=charToIso(s0,241,"n");
    	s0=charToIso(s0,242,"o");
    	s0=charToIso(s0,243,"o");
    	s0=charToIso(s0,244,"o");
    	s0=charToIso(s0,245,"o");
    	s0=charToIso(s0,246,"o");
    	s0=charToIso(s0,248,"0");
    	s0=charToIso(s0,249,"u");
    	s0=charToIso(s0,250,"u");
    	s0=charToIso(s0,251,"u");
    	s0=charToIso(s0,252,"u");
    	s0=charToIso(s0,253,"y");


  	  return s0; 
    }

	private String charToIso(String s , int i, String sr) {
		return s.replace( new Character((char)i).toString() , sr );  
	}

}
