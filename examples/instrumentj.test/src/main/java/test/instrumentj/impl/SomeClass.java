/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package test.instrumentj.impl;

/**
 * @author Stephen Evanchik (evanchsa@gmail.com)
 *
 */
public class SomeClass {

    public void aMethod() {
        System.out.println("aMethod");

        cMethod();
    }

    public void bMethod() {
        System.out.println("bMethod");
    }

    public void cMethod() {
        System.out.println("cMethod");
    }

    public void dMethod(String arg, int arg2) {
        System.out.println("dMethod: " + arg + " " + Integer.valueOf(arg2));
    }

    public void eMethod() {
    	for (int i = 0; i < 10; i++){
    		System.out.println("eMethod...:"+i);
    	}
    }

    public void fMethod(int i) {
    	while (i > 0) {
    		System.out.println("fMethod...:"+ (--i));
    	}
    }
}
