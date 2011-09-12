/**
 * Generated by Gas3 v1.1.0 (Granite Data Services) on Sat Jul 26 17:58:20 CEST 2008.
 *
 * WARNING: DO NOT CHANGE THIS FILE. IT MAY BE OVERRIDDEN EACH TIME YOU USE
 * THE GENERATOR. CHANGE INSTEAD THE INHERITED CLASS (Contact.as).
 */

package org.granite.test.tide.data {
import org.granite.test.tide.*;
import org.granite.test.tide.*;

    import flash.utils.IDataInput;
    import flash.utils.IDataOutput;
    import org.granite.meta;
    import org.granite.tide.IPropertyHolder;
    import org.granite.tide.IEntityManager;

	use namespace meta;

	[Managed]
    [RemoteClass(alias="org.granite.test.tide.data.Contact3")]
    public class Contact3 extends AbstractEntity {

        private var _email:String;
        private var _person:Person9;

        public function set email(value:String):void {
            _email = value;
        }
        public function get email():String {
            return _email;
        }

        public function set person(value:Person9):void {
            _person = value;
        }
        [Lazy]
        public function get person():Person9 {
            return _person;
        }

       	override meta function merge(em:IEntityManager, obj:*):void {
            var src:Contact3 = Contact3(obj);
            super.meta::merge(em, obj);
            if (meta::isInitialized()) {
               	em.meta_mergeExternal(src._email, _email, null, this, 'email', function setter(o:*):void{_email = o as String}) as String;
                em.meta_mergeExternal(src._person, _person, null, this, 'person', function setter(o:*):void{_person = o as Person9}) as Person;
            }
        }

        override public function readExternal(input:IDataInput):void {
            super.readExternal(input);
            if (meta::isInitialized()) {
                _email = input.readObject() as String;
                _person = input.readObject() as Person9;
            }
        }

        override public function writeExternal(output:IDataOutput):void {
            super.writeExternal(output);
            if (meta::isInitialized()) {
                output.writeObject((_email is IPropertyHolder) ? IPropertyHolder(_email).object : _email);
                output.writeObject((_person is IPropertyHolder) ? IPropertyHolder(_person).object : _person);
            }
        }
    }
}
