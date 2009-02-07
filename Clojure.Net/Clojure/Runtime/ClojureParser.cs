﻿/**
 *   Copyright (c) David Miller. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Scripting;
using clojure.lang;
using ZO.SmartCore.IO;
using System.IO;

namespace clojure.runtime
{
    class ClojureParser
    {
        private SourceUnit _source;
        private string _text;

        public ClojureParser(SourceUnit src)
        {
            _source = src;
            _text = src.GetCode();
        }

        static readonly object _eof = new object();

        internal object ParseInteractiveStatement(out ScriptCodeParseResult result)
        {
            result = ScriptCodeParseResult.Complete;
            object s = null;

            try
            {
                s = LispReader.read(new PushbackReader(new StringReader(_text)), false, _eof, false);
            }
            catch (Exception)
            {
                result = ScriptCodeParseResult.Invalid;
                s = null;
            }

            if (s == _eof)
            {
                result = ScriptCodeParseResult.IncompleteStatement;
                s = null;
            }

            return s;
        }

        internal object ParseFile()
        {
            IPersistentVector pv = PersistentVector.EMPTY;

            PushbackReader pbr = new PushbackReader(new StringReader(_text));

            pv = pv.cons(Compiler.DO);

            object eofVal = new object();
            object form;
            while ((form = LispReader.read(pbr, false, eofVal, false)) != eofVal)
                pv = pv.cons(form);

            return pv.seq();
        }
    }
}
