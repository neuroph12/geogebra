/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}

// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:

package org.mathpiper.builtin.functions.core;

import java.util.Iterator;
import java.util.Map;
import org.mathpiper.builtin.BuiltinFunction;
import org.mathpiper.lisp.Environment;
import org.mathpiper.lisp.cons.AtomCons;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.cons.ConsPointer;
import org.mathpiper.lisp.cons.SublistCons;


public class MetaKeys extends BuiltinFunction {

    public void evaluate(Environment aEnvironment, int aStackTop) throws Exception {

        ConsPointer objectPointer = new ConsPointer();
        objectPointer.setCons(getArgumentPointer(aEnvironment, aStackTop, 1).getCons());


        Map metadataMap = objectPointer.getCons().getMetadataMap();

        if (metadataMap == null || metadataMap.isEmpty()) {
            getTopOfStackPointer(aEnvironment, aStackTop).setCons(SublistCons.getInstance(aEnvironment, aEnvironment.iListAtom.copy( aEnvironment, false)));

            return;
        }//end if.


        ConsPointer consPointer = new ConsPointer();

        Cons head = aEnvironment.iListAtom.copy( aEnvironment, false);

        consPointer.setCons(head);

        java.util.Set keySet = (java.util.Set) metadataMap.keySet();

        Iterator keyIterator = keySet.iterator();

        while(keyIterator.hasNext())
        {
           String key = (String) keyIterator.next();

           Cons stringCons = AtomCons.getInstance(aEnvironment, key);

           consPointer.getCons().cdr().setCons(stringCons);

           consPointer.goNext();
           
        }//end while.
        


        getTopOfStackPointer(aEnvironment, aStackTop).setCons(SublistCons.getInstance(aEnvironment,head));




    }//end method.


}//end class.



/*
%mathpiper_docs,name="MetaKeys",categories="User Functions;Built In"
*CMD MetaKeys --- returns the metadata keys for a value or an unbound variable
*CORE
*CALL
MetaKeys(value_or_unbound_variable)

*PARMS


{value_or_unbound_variable} -- a value or an unbound variable


*DESC

Returns the metadata keys for a value or an unbound variables.  The metadata is
held in an associative list.



*SEE MetaGet, MetaSet, MetaValues, Clear
%/mathpiper_docs
 */