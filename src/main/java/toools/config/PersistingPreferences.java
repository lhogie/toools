/* (C) Copyright 2009-2013 CNRS (Centre National de la Recherche Scientifique).

Licensed to the CNRS under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The CNRS licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

*/

/* Contributors:

Luc Hogie (CNRS, I3S laboratory, University of Nice-Sophia Antipolis) 
Aurelien Lancin (Coati research team, Inria)
Christian Glacet (LaBRi, Bordeaux)
David Coudert (Coati research team, Inria)
Fabien Crequis (Coati research team, Inria)
Grégory Morel (Coati research team, Inria)
Issam Tahiri (Coati research team, Inria)
Julien Fighiera (Aoste research team, Inria)
Laurent Viennot (Gang research-team, Inria)
Michel Syska (I3S, Université Cote D'Azur)
Nathann Cohen (LRI, Saclay) 
Julien Deantoin (I3S, Université Cote D'Azur, Saclay) 

*/
 
 package toools.config;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import toools.collections.relation.HashRelation;
import toools.collections.relation.Relation;
import toools.collections.relation.RelationIO;



public class PersistingPreferences 
{
    static Relation<Integer, Relation<String, String>> r = new HashRelation<Integer, Relation<String, String>>();

    public static Relation<String, String> loadPreferencesBelongingTo(Object object)
    {
        try
        {
            File file = getFile(object);

            if (file.exists())
            {
                FileReader fis = new FileReader(file);
                Relation<String, String> r = RelationIO.load(fis);
                fis.close();
                return r;
            }
            else
            {
                file.createNewFile();
                return new HashRelation<String, String>();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }


    static public void savePreferencesBelongingTo(Object object, Relation<String, String> r)
    {
        try
        {
            File file = getFile(object);
            FileOutputStream fos = new FileOutputStream(file);
            RelationIO.save(r, fos);
            fos.close();
//            System.out.println(file.getAbsolutePath()  + " saved");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static File getFile(Object object)
    {
        return getFile(getID(object));
    }

    
    private static File getFile(int id)
    {
        File prefDir = new File(System.getProperty("user.home") + File.separator + ".lucci-flat-preferences/");
        
        if (!prefDir.exists())
        {
            prefDir.mkdirs();
        }

        return new File(prefDir.getAbsolutePath() + File.separator + id + ".relation");
    }

    private static int getID(Object object)
    {
        return Math.abs((object.getClass().getName() + object.hashCode()).hashCode());
    }

}
