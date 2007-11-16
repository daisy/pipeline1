#!/bin/sh
# the next line restarts using wish \
exec tclsh8.4 "$0" "$@"

cd [file dirname [info script]]
regsub -all {\\} $argv / argv
#puts stderr $argv

# nalign.tcl
set memdebug 0
if $memdebug {
 load c:/tclsrc/libsnack.dll
 lappend auto_path c:/tcl/lib/
 memory init on
} else {
 package require -exact snack 2.2
}
package require cmdline
#catch { package require snacksphere }

# Create phone label files *.lab
set doPhones 0
# Create word label files *.ord
set doWords 0
# Edit time tags in text file
set doTimeTags 1
# Align from lab file, skipping txt file
set realign 0
# Inter word silence mm
set addIWM 1
# Create stressed phones labels in ord file (instead of words)
set doStress 0
# Zero cross adjust boundaries
set doZeroX 0
# Do not use ensemble
set beQuick 1
# Use named HMM set
set useHMMset ""
# Ensemble method
set useMethod "default"
# Free phoneme recognition
set phoneRec 0
# Crop sound files leaving cropMargin seconds of silence at the ends
set cropMargin -1
# Start time
set startTime 0.0
# End time
set endTime -1
# Beam pruning threshold
set prunet -1
# Backtrack window
set backtrack -1
# Allow skipping of time-tags
set allowTimeSkips 0
# Allow co-articulation
set coart 0
# Lexicon file name to use for pronunciations
set lexname ""
# Set language, in reality use Waxholm or TIMIT trained models
set lang "sv"
# Save transcriptions to Transcription File Archive
set TFAname ""
# Load transcriptions from MLF file
set MLFname ""
# Text  file extension
set textFileExt .xml
# Phone label file extension
set phoneFileExt .lab
# Word label file extension
set wordFileExt .ord
# Time tagged file extension
set tagFileExt .tag

set exeName [file root $argv0]
if {$argv == ""} {
 puts "Usage: $exeName \[options\] files.list\n or    $exeName \[options\] file1 file2 ...\n\nOption\n -phones     bool  Create phone label files ($doPhones)\n -words      bool  Create word label files ($doWords)\n -timetags   bool  Edit time tags in text file ($doTimeTags)\n -realign    bool  Do realignment using phone label file ($realign)\n -iwm        bool  Try to handle spontaneous pauses ($addIWM)\n -stress     bool  Add lexical stress markers to phone labels ($doStress)\n -quick      bool  Favour speed instead of accuracy ($beQuick)\n -hmmset     name  Use specified HMM set\n -method     name  Use specified ensemble method ($useMethod)\n -phonerec   bool  Perform phoneme recognition ($phoneRec)\n -crop       time  Perform silence cropping with given margin ($cropMargin)\n -start      time  Start offset in seconds (0.0)\n -end        time  End point in seconds ('file end')\n -prune      width Apply beam pruning during search ($prunet)\n -backtrack  width Size of backtrack window ($backtrack)\n -tagskips   bool  Allow skipping of time tags ($allowTimeSkips)\n -coart      bool  Allow co-articulatory transformations ($coart)\n -lexicon    file  Use lexicon from file/save lexicon to file\n -lang       name  Use models trained for given language ($lang)\n -tfa        file  Save label files in Transcription File Archive\n -mlf        file  Read transcriptions from Master Label File\n -textext    ext   Input text file extension ($textFileExt)\n -phoneext   ext   Phone label file extension ($phoneFileExt)\n -wordext    ext   Word label file extension ($wordFileExt)\n -timetagext ext   Time tagged file extension ($tagFileExt)"
 exit
}


set debug 0
set intro ""
set grep ""

while {1} {
 if {[cmdline::getopt argv {debug.arg} opt arg] == 1} {
  set debug $arg
  continue
 }
 if {[cmdline::getopt argv {d.arg} opt arg] == 1} {
  set debug $arg
  continue
 }
 if {[cmdline::getopt argv {intro.arg} opt arg] == 1} {
  set intro $arg
  continue
 }
 if {[cmdline::getopt argv {phones.arg} opt arg] == 1} {
  if {[regexp {^[01]$} $arg] == 0} { error "'$arg' not boolean" }
  set doPhones $arg
  continue
 }
 if {[cmdline::getopt argv {words.arg} opt arg] == 1} {
  if {[regexp {^[01]$} $arg] == 0} { error "'$arg' not boolean" }
  set doWords $arg
  continue
 }
 if {[cmdline::getopt argv {timetags.arg} opt arg] == 1} {
  if {[regexp {^[01]$} $arg] == 0} { error "'$arg' not boolean" }
  set doTimeTags $arg
  continue
 }
 if {[cmdline::getopt argv {realign.arg} opt arg] == 1} {
  if {[regexp {^[01]$} $arg] == 0} { error "'$arg' not boolean" }
  set realign $arg
  set doPhones 1
  continue
 }
 if {[cmdline::getopt argv {iwm.arg} opt arg] == 1} {
  if {[regexp {^[01]$} $arg] == 0} { error "'$arg' not boolean" }
  set addIWM $arg
  continue
 }
 if {[cmdline::getopt argv {stress.arg} opt arg] == 1} {
  if {[regexp {^[01]$} $arg] == 0} { error "'$arg' not boolean" }
  set doStress $arg
  continue
 }
 if {[cmdline::getopt argv {quick.arg} opt arg] == 1} {
  if {[regexp {^[01]$} $arg] == 0} { error "'$arg' not boolean" }
  set beQuick $arg
  continue
 }
 if {[cmdline::getopt argv {hmmset.arg} opt arg] == 1} {
  set useHMMset $arg
  continue
 }
 if {[cmdline::getopt argv {method.arg} opt arg] == 1} {
  set useMethod $arg
  continue
 }
 if {[cmdline::getopt argv {phonerec.arg} opt arg] == 1} {
  if {[regexp {^[01]$} $arg] == 0} { error "'$arg' not boolean" }
  set phoneRec $arg
  continue
 }
 if {[cmdline::getopt argv {crop.arg} opt arg] == 1} {
  set cropMargin $arg
  continue
 }
 if {[cmdline::getopt argv {start.arg} opt arg] == 1} {
  set startTime $arg
  continue
 }
 if {[cmdline::getopt argv {end.arg} opt arg] == 1} {
  set endTime $arg
  continue
 }
 if {[cmdline::getopt argv {prune.arg} opt arg] == 1} {
  set prunet $arg
  continue
 }
 if {[cmdline::getopt argv {backtrack.arg} opt arg] == 1} {
  set backtrack $arg
  continue
 }
 if {[cmdline::getopt argv {tagskips.arg} opt arg] == 1} {
  if {[regexp {^[01]$} $arg] == 0} { error "'$arg' not boolean" }
  set allowTimeSkips $arg
  continue
 }
 if {[cmdline::getopt argv {coart.arg} opt arg] == 1} {
  if {[regexp {^[01]$} $arg] == 0} { error "'$arg' not boolean" }
  set coart $arg
  continue
 }
 if {[cmdline::getopt argv {lexicon.arg} opt arg] == 1} {
  set lexname $arg
  continue
 }
 if {[cmdline::getopt argv {inputXML.arg} opt arg] == 1} {
  set inXML $arg
  continue
 }
 if {[cmdline::getopt argv {inputAudio.arg} opt arg] == 1} {
  set inAudio $arg
  continue
 }
 if {[cmdline::getopt argv {inputLanguage.arg} opt arg] == 1} {
  set lang $arg
  continue
 }
 if {[cmdline::getopt argv {resultPath.arg} opt arg] == 1} {
  set outXML $arg
  continue
 }
 if {[cmdline::getopt argv {tfa.arg} opt arg] == 1} {
  set TFAname $arg
  continue
 }
 if {[cmdline::getopt argv {mlf.arg} opt arg] == 1} {
  set MLFname $arg
  continue
 }
 if {[cmdline::getopt argv {grep.arg} opt arg] == 1} {
  set grep $arg
  continue
 }
 if {[cmdline::getopt argv {textext.arg} opt arg] == 1} {
  set textFileExt .[string trimleft $arg .]
  continue
 }
 if {[cmdline::getopt argv {phoneext.arg} opt arg] == 1} {
  set phoneFileExt .[string trimleft $arg .]
  continue
 }
 if {[cmdline::getopt argv {wordext.arg} opt arg] == 1} {
  set wordFileExt .[string trimleft $arg .]
  continue
 }
 if {[cmdline::getopt argv {timetagext.arg} opt arg] == 1} {
  set tagFileExt .[string trimleft $arg .]
  continue
 }
 break
}

if {$useHMMset != ""} {
 set beQuick 1
 if {[file exists $useHMMset.tcl]} {
  source $useHMMset.tcl
 }
}
if {$useMethod != "default"} {
 set beQuick 0
}
if {$backtrack > 250} {
 puts "Warning, -backtrack parameter is probably too large"
}
if {$backtrack > 0} {
 set backtrack [expr int(100*$backtrack)]
}

if {$phoneRec} {
 set beQuick 1
 set doWords 0
 set doPhones 1
 if {$useHMMset == ""} {
  set useHMMset "1025_8mix"
 }
}
if {$beQuick == 0} {
 source align.tree
}
if {$lang == "sv"} {
 set lang "se"
}
if {$doTimeTags} {
 set beQuick 1
 if {$backtrack == -1} {
  set backtrack 3000
 }
 if {$useHMMset == ""} {
  if {$lang == "se"} {
   set useHMMset "1025_8mix"
  } else {
   set useHMMset "t1016"
  }
 }
}

# text analysis
#source libta.tcl
#ta::init

# generate list of tokens (words) from input text
# html.like tags should generate one token each, regardless of content
proc tokenise {text} {
 # xxx<tag>xxx -> xxx <tag> xxx
 regsub -all {(\S)\<(\S)} $text {\1 <\2} text
 regsub -all {(\S)\>(\S)} $text {\1> \2} text
 set in [split $text]
 set out {}
 set insideTag 0

 foreach token $in {
  if {$token == ""} continue
  if {$insideTag} {
   set out [lreplace $out end end "[lindex $out end] $token"]
  } else {
   lappend out $token
  }
  if {[regexp {\<\S+} $token]} {
   set insideTag 1
  }
  if {[regexp {\S+\>} $token]} {
   set insideTag 0
  }
 }

 return $out
}

# XML parsing

package require -exact xml 3.1

proc formatTime {t} {
 set dec [string trimleft [format "%.3f" [expr {$t-int($t)}]] 0]
 if {$dec == 1} {
  set t [expr {$t + 1.0}]
 }
 set tmp [clock format [expr {int($t)}] -format "%H:%M:%S" -gmt 1]
 if {$dec == 1} {
  set tmp ${tmp}[string trimleft [format "%.3f" 0.0] 0]
 } else {
  set tmp ${tmp}$dec
 }
 
 return $tmp
}

set lastSyncElem ""
set lastSSMLTran ""
set lastSSMLType ""
set lastSSMLElem ""
set lastSSMLWord ""

proc EStart {name attlist args} {
 array set a [list -empty 0 \
		  -namespace "" \
		  -namespacedecls {}]
 array set a $args

 # first pass, insert <time>
 if {$::parserMode == "getText"} {
  if {[lsearch $attlist smil:sync] > -1} {
   set ::lastSyncElem $name
   append ::text {<time>}
  }
  foreach {attr val} $attlist {
   if {$attr == "ph"} {
    set ::lastSSMLTran $val
    set ::lastSSMLElem $name
   }
   if {$attr == "type" && $val == "english"} {
    set ::lastSSMLType "english"
   }
   if {$attr == "type" && $val == "proper name"} {
    set ::lastSSMLType "proper name"
   }
  }
 }

 # second pass, create smil-attributes 
 if {$::parserMode == "insertTime"} {
  # relate namespace to prefix
  if {$a(-namespace) != ""} {
   foreach {x y} $a(-namespacedecls) {
    set ::ns($x) $y
   }
  }
  append ::text "<"
  if {$a(-namespace) != "" && $::ns($a(-namespace)) != ""} {
   append ::text "$::ns($a(-namespace)):"
  }
  append ::text "$name"

  # attribute list
  foreach {attr val} $attlist {
   regsub {\"} $val {\&quot;} val
   append ::text " $attr=\"$val\""
  }
  foreach {x y} $a(-namespacedecls) {
   if {$y != ""} {
    append ::text " xmlns:$y=\"$x\""
   } else {
    append ::text " xmlns=\"$x\""
   }
  }
  if {[lsearch $attlist smil:sync] > -1} {
   set ::lastSyncElem $name
   set begin [formatTime [lindex $::tmp $::tmpIndex]]
   incr ::tmpIndex
   append ::text " smil:clipBegin=\"$begin\""
   set end [formatTime [lindex $::tmp $::tmpIndex]]
   incr ::tmpIndex
   append ::text " smil:clipEnd=\"$end\""
   append ::text " smil:src=\"$::inAudio\""
  }
  if {$a(-empty)} { append ::text " /" }

  append ::text ">"
 }
}

proc EEnd {name args} {
 array set a [list -empty 0 -namespace ""]
 array set a $args

 if {$a(-empty)} return

 if {$::parserMode == "getText"} {
  if {$name == $::lastSyncElem} {
   #append ::text "</time>"
  }
  set ::lastSyncElem ""
  if {$name == $::lastSSMLElem} {
   if {$::lastSSMLTran != ""} {
    set ::trans($::lastSSMLWord) [list $::lastSSMLTran]
   }
   if {$::lastSSMLType != ""} {
    set ::domain($::lastSSMLWord) $::lastSSMLType
   }
   set ::lastSSMLTran ""
   set ::lastSSMLElem ""
   set ::lastSSMLWord ""
   set ::lastSSMLType ""
  }
 }
 
 if {$::parserMode == "insertTime"} {
  append ::text "</"
  if {$a(-namespace) != ""} {
   if {$::ns($a(-namespace)) != ""} {
    append ::text "$::ns($a(-namespace)):"
   }
  }
  append ::text "$name>"
 }
}

proc ECharData {data} {
 if {$::parserMode == "insertTime"} {
  regsub -all {&} $data {\&amp;} data
  regsub -all {<} $data {\&lt;} data
  regsub -all {>} $data {\&gt;} data
  regsub -all {'} $data {\&apos;} data
  regsub -all {"} $data {\&quot;} data
 } else {
  regsub -all {\&amp;}  $data {&} data
  regsub -all {\&lt;}   $data {<} data
  regsub -all {\&gt;}   $data {>} data
  regsub -all {\&apos;} $data {'} data
  regsub -all {\&quot;} $data {"} data
 }
 if {$::lastSSMLElem != ""} {
  set ::lastSSMLWord $data
 }
 append ::text $data
}

proc EXML {version encoding standalone} {
 append ::text "<?xml version=\'$version\' encoding=\'$encoding\'?>"
}

set parser [::xml::parser -reportempty 1 \
		-elementstartcommand EStart \
		-characterdatacommand ECharData \
		-processinginstructioncommand EProcess \
		-xmldeclcommand EXML \
		-elementendcommand EEnd]

set fd [open $::inXML]
fconfigure $::fd -encoding utf-8
set data [read -nonewline $::fd]
set dataCopy $data
close $::fd

set text ""
set parserMode getText
$parser parse $data

if 0 {
set eee [open kk.txt w]
fconfigure $eee -encoding utf-8
puts $eee $text
close $eee
}
#puts qq,$text

# lts
source liblts.tcl
if {$lang == "se"} {
 #lts::init $lang suot.tree suot.except
 # 070621
 lts::init $lang all.tree tpblex.except
 source lexica.tcl
} else {
 set lang us
 lts::init $lang cmu.tree cmu.except
}

#pack [label .a -text "Creating database..."]
#wm withdraw .
#update

set phones [list sil gbg asp Öh RD RL RN RS RT A Å Ä3 Ä4 Ä A: Ä: Å: B b D d E E0 E: F G g H I I: J K k L M N NG O Ö Ö3 Ö4 Ö: O: P p R S SJ T t TJ U U: V Y Y:]

proc adjustForClosures {stress trans} {
 #puts [info level 0]
 set res {}
 foreach st $stress tr $trans {
  set res2 {}
  foreach s $st {
   for {set i 0} {$i < $s} {incr i} {
    if {[lsearch {pcl tcl kcl bcl dcl gcl rdc rtc} [lindex $tr $i]] >= 0} {
     incr s
    }
   }
   lappend res2 $s
  }
  lappend res $res2
 }
 #puts ->$res
 return $res
}

proc getStressV {tran} {
 #puts [info level 0]
 set n [llength [lindex $tran 0]]
 set res {}
 for {set i 0} {$i < $n} {incr i} {
  set tmp [lindex [lindex $tran 0] $i]
  regsub -all -- {-\s} $tmp "" tmp
  set a [lsearch $tmp *_1]
  set b [lsearch $tmp *_2]
  set c [lsearch $tmp *_3]
  lappend res [list $a $b $c]
 }
 return $res
}

proc CreateLexicon {} {
 foreach file $::files {
  #set f [open $::inXML]
  #set text [read $f]
  #close $f
  set text [TextNorm $::text 0]
  foreach word [tokenise $text] {
   if {[regexp {[\w]+} $word]} {
    set dic($word) 17
   }
  }
 }
#puts qq,$text
 set dic(<silence>) 17
 set dic(<garbage>) 17
 set dic(<speech>) 17
 
 foreach word [lsort [array names dic *]] {
  if {[info exists ::trans($word)]} continue
  if {$word == "<silence>"} {
   set ::trans($word) "sil"
   set ::stressV($word) [list [list -1 -1 -1]]
  } elseif {$word == "<garbage>"} {
   set ::trans($word) "gbg"
   set ::stressV($word) [list [list -1 -1 -1]]
  } elseif {$word == "<speech>"} {
   set ::trans($word) ""
   set ::stressV($word) [list [list -1 -1 -1]]
  } elseif {$word == "<eh>"} {
   set ::trans($word) "Öh"
   set ::stressV($word) [list [list -1 -1 -1]]
  } elseif {$word == "<ehm>"} {
   set ::trans($word) "M"
   set ::stressV($word) [list [list -1 -1 -1]]
  } elseif {$word == "<breath>"} {
   set ::trans($word) "asp"
   set ::stressV($word) [list [list -1 -1 -1]]
  } elseif {[string match <*> $word]} {
   # unknown <xxx> tags will just be removed from phone/word label files
   # since no transcription is created here
   set ::trans($word) ""
   set ::stressV($word) ""
  } else {
   regsub -all {,|\.|!|\?} $word "" clean_word
   if {$::debug > 2} {
    puts  "$word: "
    puts "[lts::syn2rec [lts::transcribe -stress 1 -multiple 1 $clean_word]],\
	[lts::transcribe -stress 1 -multiple 1 $clean_word], $clean_word"
   }
   #if {[string match *-* [lts::transcribe -stress 1 -multiple 1 $clean_word]]} {
   # puts $word,[lts::transcribe -stress 1 -multiple 1 $clean_word]
   #}
   # bättre att bara göra ett anrop till lts::trans och ta bort stress här
   if {[info exists ::domain($clean_word)]} {
    set domain $::domain($clean_word)
   } else {
    set domain default
   }
   set ::trans($word)   [lts::syn2rec [lindex [lts::transcribe -multiple 1 \
					    -domain $domain $clean_word] 0]]
   if {$::doStress} {
    set ::stressV($word) [adjustForClosures [getStressV [lts::transcribe \
	     -domain $domain -stress 1 -multiple 1 $clean_word]] $::trans($word)]
   }
   #puts "$word $::trans($word) $::stressV($word)"
   #puts [lts::transcribe -stress 1 -multiple 1 $clean_word]
   #puts [getStressV [lts::transcribe -stress 1 -multiple 1 $clean_word]]
  }
 }
 set ::trans(<time>) "sil"
 set ::stressV(<time>) [list [list -1 -1 -1]]
}

proc SaveLexicon filename {
 set f [open $filename w]
 foreach word [lsort [array names ::trans *]] {
  foreach alt $::trans($word) {
   puts $f "lappend trans($word) \[list $alt\]"
  }
  if {[info exists ::stressV($word)]} {
   foreach alt $::stressV($word) {
    puts $f "lappend stressV($word) \[list $alt\]"
   }
  }
 }
 close $f
}

proc walkTree {dt} {
 switch -- [llength $dt] {
  0 -
  1 -
  2 {return $dt}
  3 {
   set match 0
   set question     [lindex $dt 0]
   set name         [lindex $question 0]
   set context      [lindex $question 1]
   set pattern      [lindex $question 2]

   if {[regexp $pattern [lindex $::featVect $context]]} {
    set match 1
   }

   if {$match} {
    walkTree [lindex $dt 1]
   } else {
    walkTree [lindex $dt 2]
   }
  }
  default {error "bad dtree $dt"}
 }
}

proc TextNorm {text isLastChunk} {
 if {1 || $::lexname != "" || $::lang == "us"} {
  set tmp $text
 } else {
  #set tmp [ta::analyse $text]
 }
 set text ""
 foreach word [tokenise $tmp] {
  if {[string match <*> $word] == 0} {
   #regsub -all {\'|\`|\"|,|\.|!|\?|\;|©|\]|\[|\|} $word "" word
   regsub -all {\'|\`|\"|\;|©|\]|\[|\|} $word "" word
  } else {
   # this would be useful for removing html tags...
   # set word ""
  }
  append text $word " "
 }
 set ntimetags [regsub -all {<time[\w\s\"\.\=]*>} $text "<time>" text]
 regsub -all {</time[\w\s]*>} $text "" text
 # Special handling of last <time> tag 
 if {$isLastChunk && $ntimetags > 0} {
  append text " <speech>"
 }
 return $text
}

set allowedHmmNames [list RS RT RL RN RD TJ SJ NG Ö3 Ö4 Ä3 Ä4 A: I: E: O: U: Y: Å: Ä: Ö: A0 E0 F V S H M N R L J E A I O U Y Å Ä Ö P B T D K G p t k b d g sil]

proc tpa2hmm {inTrans} {
 if {$::lang != "se"} { return $inTrans }
 regsub {e\.} $inTrans "E:" trans
 
 regsub {sil} $trans "SIL"  trans
 
 regsub {\.} $trans ""  trans
 regsub {2:} $trans ":" trans
 regsub {2}  $trans ":" trans
 
 # Xenophone mappings
 regsub {3:} $trans "3" trans
 regsub {öw}  $trans "Ö3" trans ;  # 070621
 
 regsub {an:} $trans "A:" trans
 regsub {an}  $trans "A:" trans
 regsub {a3}  $trans "A:" trans
 regsub {dh:} $trans "V"  trans
 regsub {dh}  $trans "V"  trans
 regsub {en:} $trans "Ä:" trans
 regsub {en}  $trans "Ä:" trans
 regsub {tj3} $trans "TJ" trans
 regsub {sj3} $trans "SJ" trans
 regsub {j3}  $trans "J"  trans
 regsub {on}  $trans "Å:" trans
 regsub {r3}  $trans "R"  trans
 regsub {r4}  $trans "R"  trans
 regsub {rs3} $trans "RS" trans
 regsub {th:} $trans "F"  trans
 regsub {th}  $trans "F"  trans
 regsub {u4:} $trans "O:" trans
 regsub {u4}  $trans "O:" trans
 regsub {w:}  $trans "O:" trans
 regsub {w}   $trans "O:" trans
 regsub {z}   $trans "RS" trans
 
 # Swedish phone mappings
 
 regsub {ä3:} $trans "Ä3" trans
 regsub {ä3} $trans "Ä4"  trans
 regsub {ö3:} $trans "Ö3" trans
 regsub {ö3} $trans "Ö4"  trans
 regsub {ë} $trans "E0"   trans
 regsub {e3} $trans "E" trans
 regsub {u3} $trans "U" trans
 regsub {o3} $trans "O" trans
 regsub {i3} $trans "I" trans
 
 regsub {au}  $trans "A" trans
 regsub {eu}  $trans "E" trans
 
 regsub {u:}  $trans "U:" trans
 regsub {u}  $trans "U"   trans
 regsub {o:}  $trans "O:" trans
 regsub {o}  $trans "O"   trans
 regsub {a:}  $trans "A:" trans
 regsub {a}  $trans "A"   trans
 regsub {e:}  $trans "E:" trans
 regsub {e}  $trans "E"   trans
 regsub {i:}  $trans "I:" trans
 regsub {i}  $trans "I"   trans
 regsub {y:}  $trans "Y:" trans
 regsub {y}  $trans "Y"   trans
 regsub {å:}  $trans "Å:" trans
 regsub {å}  $trans "Å"   trans
 regsub {ä:}  $trans "Ä:" trans
 regsub {ä}  $trans "Ä"   trans
 regsub {ö:}  $trans "Ö:" trans
 regsub {ö}  $trans "Ö"   trans
 
 regsub {rd:} $trans "d"  trans
 regsub {ng:} $trans "NG" trans
 regsub {b:}  $trans "b"  trans
 regsub {d:}  $trans "d"  trans
 regsub {g:}  $trans "g"  trans
 regsub {h:}  $trans "H"  trans
 regsub {f:}  $trans "F"  trans
 regsub {tj:} $trans "TJ" trans
 regsub {sj:} $trans "SJ" trans
 regsub {j:}  $trans "J"  trans
 regsub {k:}  $trans "k"  trans
 regsub {rl:} $trans "RL" trans
 regsub {l:}  $trans "L"  trans
 regsub {m:}  $trans "M"  trans
 regsub {rn:} $trans "RN" trans
 regsub {n:}  $trans "N"  trans
 regsub {p:}  $trans "p"  trans
 regsub {r:}  $trans "R"  trans
 regsub {rs:} $trans "RS" trans
 regsub {s:}  $trans "S"  trans
 regsub {rt:} $trans "t"  trans
 regsub {t:}  $trans "t"  trans
 regsub {v:}  $trans "V"  trans 
 
 regsub {rtc} $trans "RT" trans
 regsub {rdc} $trans "RD" trans
 
 regsub {rd} $trans "d"  trans
 regsub {rl} $trans "RL" trans
 regsub {rn} $trans "RN" trans
 regsub {rs} $trans "RS" trans
 regsub {rt} $trans "t"  trans
 
 regsub {bcl} $trans "B" trans
 regsub {dcl} $trans "D" trans
 regsub {gcl} $trans "G" trans
 regsub {h}  $trans "H"  trans
 regsub {f}  $trans "F"  trans
 regsub {tj} $trans "TJ" trans
 regsub {j}  $trans "J"  trans
 regsub {kcl} $trans "K" trans
 regsub {pcl} $trans "P" trans
 regsub {tcl} $trans "T" trans
 regsub {rl} $trans "RL" trans
 regsub {l}  $trans "L"  trans
 regsub {m}  $trans "M"  trans
 regsub {ng} $trans "NG" trans
 regsub {rn} $trans "RN" trans
 regsub {n}  $trans "N"  trans
 regsub {rs} $trans "RS" trans
 regsub {r}  $trans "R"  trans
 regsub {sj} $trans "SJ" trans
 regsub {s}  $trans "S"  trans
 regsub {v}  $trans "V"  trans

 regsub {s\s|t\s|r\s|n\s} $trans {& } trans
# set trans [string toupper $trans]
 regsub {SIL} $trans "sil" trans
 if {[lsearch $::allowedHmmNames $trans] == -1} { set trans "" ; puts "Disallowing: $inTrans -> $trans" }
 return $trans
}

proc CreateLists {text plist alist plen isLastChunk} {
 upvar #0 $plist phonelist
 upvar #0 $alist arclist
 upvar #0 $plen longest

 set phonelist {!NULL {sil "sil <silence>"} !NULL}
 set arclist {{0 1} {1 2} {0 2}}
 set longest 0

 set nodeindex 2
 set wordend 2
 set lastword ""
 set words [tokenise $text]
 set nWords [llength $words]
 set tmp {}
 
 for {set j 0} {$j < $nWords} {incr j} {
  set word [lindex $words $j]
  if {$word == ""} continue
  if {[info exists ::trans($word)] == 0} {
   #puts stderr "Skipping word: $word ($::file)"
   set ::trans($word) {}
   set ::stressV($word) {}
   continue
  }
  lappend tmp $word
 }
 set words $tmp
 set nWords [llength $words]

 for {set j 0} {$j < $nWords} {incr j} {
  if {[string equal [lindex $words $j] "<silence>"] || \
	  [string equal [lindex $words $j] "<time>"]} continue
  break
 }

 set nwtrans  $::trans([lindex $words $j])
 if {$::doStress} {
  set nstressV $::stressV([lindex $words $j])
 } else {
  set nstressV ""
 }
 
 # Create FSTN word for word
 
 for {set j 0} {$j < $nWords} {incr j} {
  set word [lindex $words $j]
  if {[string equal $lastword "<silence>"] && \
	  [string equal $word "<silence>"]} continue
  set start $wordend
  set end [expr $nodeindex+1]
  set wtrans  $nwtrans
  set stressV $nstressV
  
  # Special sub-network for <speech> and <time>
  if {[string equal $word "<speech>"] || \
    [string equal $word "<time>"]} {
   if {0&&$::backtrack <= 0} {
    lappend phonelist "sil \"sil $word\""
    lappend arclist [list $start [incr nodeindex]]
    lappend phonelist "!NULL"
    lappend arclist [list $nodeindex [incr nodeindex]]
    set nullstart $nodeindex
    
    for {set k 0} {$k < [llength $::phones]} {incr k} {
     lappend phonelist "[lindex $::phones $k] [lindex $::phones $k]"
     lappend arclist [list $nullstart [expr $nullstart + $k + 1]]
     lappend arclist [list [expr $nullstart + $k + 1] \
       [expr $nullstart + [llength $::phones] + 1]]
     incr nodeindex
    }
    lappend phonelist "!NULL"
    incr nodeindex
    lappend arclist [list $nodeindex $nullstart] ;# close phoneme loop
    lappend arclist [list $nullstart $nodeindex] ;# skip phoneme loop
    set wordend $nodeindex
    continue
    incr longest
   } else {
    list {
     lappend phonelist "sil \"sil $word\""
     lappend arclist [list $start [incr nodeindex]]
     lappend phonelist "!NULL"
     lappend arclist [list $nodeindex [incr nodeindex]]
     set allStart $nodeindex
     lappend phonelist "all all"
     lappend arclist [list $allStart [incr nodeindex]]
     lappend phonelist "!NULL"
     lappend arclist [list $nodeindex [incr nodeindex]]
     set allEnd $nodeindex
    
     # extra sil-all
     lappend phonelist "sil sil" "all all"
     lappend arclist [list $allEnd [incr nodeindex]]
     lappend arclist [list $nodeindex [incr nodeindex]]
     lappend arclist [list $nodeindex $allEnd]
     
     # skip iwm branch
     lappend arclist [list $allStart $allEnd]
     set wordend $allEnd
     
     # skip whole sentence from last <time>
     if {[info exists allLast]} {
      #     lappend arclist [list $allLast $allStart]
     }
     #    set allLast $allStart
    }

    if {$::allowTimeSkips} {
     # skip whole sentence from last <time>
     lappend phonelist "!NULL"
     lappend arclist [list $start [incr nodeindex]]
     if {[info exists allStart]} {
      lappend arclist [list $allStart $nodeindex]
     }
     set start $nodeindex
    }
    lappend phonelist "sil \"sil $word\""
    lappend arclist [list $start [incr nodeindex]]
    lappend phonelist "!NULL"
    lappend arclist [list $nodeindex [incr nodeindex]]
    set allStart $nodeindex
    lappend phonelist "all all"
    lappend arclist [list $allStart [incr nodeindex]]
    lappend phonelist "!NULL"
    lappend arclist [list $nodeindex [incr nodeindex]]
    set allEnd $nodeindex
    
    # extra sil-all
    lappend phonelist "sil sil" "all all"
    lappend arclist [list $allEnd [incr nodeindex]]
    lappend arclist [list $nodeindex [incr nodeindex]]
    lappend arclist [list $nodeindex $allEnd]
    #litet test xxx
    lappend phonelist "asp asp"
    lappend arclist [list $allEnd [incr nodeindex]]
    lappend arclist [list $nodeindex $allEnd]

    # skip iwm branch
    lappend arclist [list $allStart $allEnd]
    set wordend $allEnd

    continue
   }
  }

  if {$j < $nWords - 1} {
   set nextw [lindex $words [expr {$j+1}]]

   if {[string equal $nextw "<silence>"] || [string equal $nextw "<time>"]} {
    set nextw [lindex $words [expr {$j+2}]]
   }
   if {$nextw != ""} {
    set nwtrans $::trans($nextw)
   } else {
    set nwtrans {}
    set nstressV {}
   }
   if {$::doStress} {
    set nstressV $::stressV($nextw)
   } else {
    set nstressV {}
   }
  } else {
   set nextw ""
   set nwtrans {}
   set nstressV {}
  }
  #if {$nextw == "TPB"} {puts QQ,$nwtrans }
  #if {$nextw == "Sweden."} {puts QQ,$nwtrans }
  #if {$nextw == "use"} {puts QQ,$nwtrans }

  if {$::coart} {
#   puts AAA,$word,$wtrans,,,,,$nwtrans
   lts::coart $word $nextw wtrans nwtrans stressV nstressV
#   puts bbb,$word,$wtrans,,,,,$nwtrans,\n
  }

  # Single pronunciation variant
  
  if {[llength $wtrans] == 1} {
   set transcription [lindex $wtrans 0]
   for {set k 0} {$k < [llength $transcription]} {incr k} {
    set phone [lindex $transcription $k]
    set hmmName [tpa2hmm $phone]
    if {$hmmName == ""} { error "$word: $phone ($transcription)" }
    set tag $phone
    if {$k == 0 || $::doStress} {
     if {$k == 0} {
      append tag " " $word
     }
     if {$::doStress} {
      set pos [lsearch [lindex $stressV 0] $k]
      if {$pos >= 0} {
       append tag _[expr $pos+1]
      }
     }
    }
    lappend phonelist "$hmmName \"$tag\""
    lappend arclist [list $start $end]
    incr nodeindex
    set start $nodeindex
    set end [expr $nodeindex+1]
    incr longest
   }
   if {$::addIWM && $j < $nWords - 1 && \
	   [string equal $word "<silence>"] == 0} {
    lappend phonelist "!NULL"
    lappend arclist [list $nodeindex [incr nodeindex]]
    set iwstart $nodeindex
    lappend phonelist "sil \"sil <silence>\""
    lappend arclist [list $iwstart [incr nodeindex]]
    lappend phonelist "!NULL"
    lappend arclist [list $nodeindex [incr nodeindex]]
    set iwend $nodeindex

    if 0 {
     lappend phonelist "asp asp <breath>\""
     lappend arclist [list $iwstart [incr nodeindex]]
     lappend arclist [list $nodeindex $iwend]
     
     lappend phonelist "sil sil <silence>" "asp asp <breath>" "sil sil <silence>"
     lappend arclist [list $iwstart [incr nodeindex]]
     lappend arclist [list $nodeindex [incr nodeindex]]
     lappend arclist [list $nodeindex [incr nodeindex]]
     lappend arclist [list $nodeindex $iwend]
    }
    # skip iwm branch
    lappend arclist [list $iwstart $iwend]
    set wordend $iwend
   } else {
    set wordend [expr $end-1]
   }
   set lastword $word
   continue
  }

  # Multiple pronunciation variants

  if {[llength $wtrans] > 1 && [lindex $phonelist end] != "!NULL"} {
   lappend phonelist "!NULL"
   lappend arclist [list $start [incr nodeindex]]
  }
  set nullflag 0
  set wordstart $nodeindex
  set nAlt 0
  set maxlength 0
  foreach transalt $wtrans {
   set phoneindex 0
   set transalt [lindex $wtrans $nAlt]
   for {set k 0} {$k < [llength $transalt]} {incr k} {
    set phone [lindex $transalt $k]
    set hmmName [tpa2hmm $phone]
    if {$hmmName == ""} { error "$word: $phone ($transalt)" }
    set tag $phone
    if {$phoneindex == 0} {
     append tag " " $word
    }
    if {$::doStress} {
     set pos [lsearch [lindex $stressV $nAlt] $k]
     if {$pos >= 0} {
      append tag _[expr $pos+1]
     }
    }
    if {$phoneindex == 0} {
     # Add word tag on first phone
     lappend phonelist "$hmmName \"$tag\""
     lappend arclist [list $wordstart [expr $nodeindex+1]]
    } else {
     lappend phonelist "$hmmName \"$tag\""
     lappend arclist [list $nodeindex [expr $nodeindex+1]]
    }
    if {$phoneindex == [llength $transalt] - 1 && $nAlt} {
     lappend arclist [list [expr $nodeindex+1] $wordend]
    }
    incr nodeindex
    incr phoneindex
   }
   if {[llength $wtrans] > 1 && $nullflag == 0} { 
    lappend phonelist "!NULL"
    lappend arclist [list $nodeindex [incr nodeindex]]
    set nullflag 1
    set wordend $nodeindex
   }
   incr nAlt
   if {$maxlength < [llength $transalt]} {
    set maxlength [llength $transalt]
   }
  } ;# end foreach transalt
  set lastword $word
  incr longest $maxlength
  if {$::addIWM && $j < $nWords - 1 && \
	  [string equal $word "<silence>"] == 0} {
   set iwstart $wordend
   lappend phonelist "sil \"sil <silence>\""
   lappend arclist [list $iwstart [incr nodeindex]]
   lappend phonelist "!NULL"
   lappend arclist [list $nodeindex [incr nodeindex]]
   set iwend $nodeindex

   if 0 {
    lappend phonelist "asp asp <breath>"
    lappend arclist [list $iwstart [incr nodeindex]]
    lappend arclist [list $nodeindex $iwend]
    
    lappend phonelist "sil sil <silence>" "asp asp <breath>" "sil sil <silence>"
    lappend arclist [list $iwstart [incr nodeindex]]
    lappend arclist [list $nodeindex [incr nodeindex]]
    lappend arclist [list $nodeindex [incr nodeindex]]
    lappend arclist [list $nodeindex $iwend]
   }
   
   # skip iwm branch
   lappend arclist [list $iwstart $iwend]
   set wordend $iwend
  }
 } ;# end foreach word
 # Make sure there is silence at end of file
 if {$lastword != "<silence>" && $isLastChunk} {
  lappend phonelist "!NULL" {sil "sil <silence>"} "!NULL"
  lappend arclist [list $wordend [expr $nodeindex+1]]
  lappend arclist [list [expr $nodeindex+1] [expr $nodeindex+2]]
  lappend arclist [list [expr $nodeindex+2] [expr $nodeindex+3]]
  lappend arclist [list [expr $nodeindex+1] [expr $nodeindex+3]]
 }

 if {$::debug > 1} {
  set i 0
  set f [open _st.txt w]
  foreach s $phonelist {
   regsub -all {\s} $s "" s
   puts $f "${s}_$i $i"
   incr i
  }
  close $f
  set f [open _ar.txt w]
  foreach a $arclist {
   foreach {s e} $a break
   set ss [lindex $phonelist $s]
   set es [lindex $phonelist $e]
   regsub -all {\s} $ss "" ss
   regsub -all {\s} $es "" es
   puts $f "${ss}_$s ${es}_$e 0"
  }
  close $f
 }
}

proc VerifyCreateLists {} {

 foreach word [list "och" "onödan"] {
  set ::trans($word)  [lts::syn2rec [lts::transcribe -multiple 1 $word]]
 }


 set ::phonelist {}
 set ::arclist {}
 set phonelen 0
 CreateLists "onödan" ::phonelist ::arclist phonelen 1
 if {$::phonelist != {!NULL {sil <silence>} !NULL {O: onödan} N Ö: D d A N !NULL {sil <silence>} !NULL}} {
  puts $::phonelist
 }
 if {$::arclist != {{0 1} {1 2} {0 2} {2 3} {3 4} {4 5} {5 6} {6 7} {7 8} {8 9} {9 10} {10 11} {11 12} {10 12}}} {
  puts $::arclist
 }


 set ::phonelist {}
 set ::arclist {}
 set phonelen 0
 CreateLists "onödan onödan" ::phonelist ::arclist phonelen 1
 if {$::phonelist != {!NULL {sil <silence>} !NULL {O: onödan} N Ö: D d A N !NULL {sil <silence>} !NULL {asp <breath>} {sil <silence>} {asp <breath>} {sil <silence>} {O: onödan} N Ö: D d A N !NULL {sil <silence>} !NULL}} {
  puts $::phonelist
 }
 if {$::arclist != {{0 1} {1 2} {0 2} {2 3} {3 4} {4 5} {5 6} {6 7} {7 8} {8 9} {9 10} {10 11} {11 12} {10 13} {13 12} {10 14} {14 15} {15 16} {16 12} {10 12} {12 17} {17 18} {18 19} {19 20} {20 21} {21 22} {22 23} {23 24} {24 25} {25 26} {24 26}}} {
  puts $::arclist
 }


 set ::phonelist {}
 set ::arclist {}
 set phonelen 0
 CreateLists "onödan onödan onödan" ::phonelist ::arclist phonelen 1
 if {$::phonelist != {!NULL {sil <silence>} !NULL {O: onödan} N Ö: D d A N !NULL {sil <silence>} !NULL {asp <breath>} {sil <silence>} {asp <breath>} {sil <silence>} {O: onödan} N Ö: D d A N !NULL {sil <silence>} !NULL {asp <breath>} {sil <silence>} {asp <breath>} {sil <silence>} {O: onödan} N Ö: D d A N !NULL {sil <silence>} !NULL}} {
  puts $::phonelist
 }
 if {$::arclist != {{0 1} {1 2} {0 2} {2 3} {3 4} {4 5} {5 6} {6 7} {7 8} {8 9} {9 10} {10 11} {11 12} {10 13} {13 12} {10 14} {14 15} {15 16} {16 12} {10 12} {12 17} {17 18} {18 19} {19 20} {20 21} {21 22} {22 23} {23 24} {24 25} {25 26} {24 27} {27 26} {24 28} {28 29} {29 30} {30 26} {24 26} {26 31} {31 32} {32 33} {33 34} {34 35} {35 36} {36 37} {37 38} {38 39} {39 40} {38 40}}} {
  puts $::arclist
 }


 set ::phonelist {}
 set ::arclist {}
 set phonelen 0
 CreateLists "och" ::phonelist ::arclist phonelen 1
 if {$::phonelist != {!NULL {sil <silence>} !NULL {Å: och} !NULL {Å och} {Å och} K k !NULL {sil <silence>} !NULL}} {
  puts $::phonelist
 }
 if {$::arclist != {{0 1} {1 2} {0 2} {2 3} {3 4} {2 5} {5 4} {2 6} {6 7} {7 8} {8 4} {4 9} {9 10} {10 11} {9 11}}} {
  puts $::arclist
 }


 set ::phonelist {}
 set ::arclist {}
 set phonelen 0
 CreateLists "och och" ::phonelist ::arclist phonelen 1
 if {$::phonelist != {!NULL {sil <silence>} !NULL {Å: och} !NULL {Å och} {Å och} K k {sil <silence>} !NULL {asp <breath>} {sil <silence>} {asp <breath>} {sil <silence>} !NULL {Å: och} !NULL {Å och} {Å och} K k !NULL {sil <silence>} !NULL}} {
  puts $::phonelist
 }
 if {$::arclist != {{0 1} {1 2} {0 2} {2 3} {3 4} {2 5} {5 4} {2 6} {6 7} {7 8} {8 4} {4 9} {9 10} {4 11} {11 10} {4 12} {12 13} {13 14} {14 10} {4 10} {10 15} {15 16} {16 17} {15 18} {18 17} {15 19} {19 20} {20 21} {21 17} {17 22} {22 23} {23 24} {22 24}}} {
  puts $::arclist
 }


 set ::phonelist {}
 set ::arclist {}
 set phonelen 0
 CreateLists "och onödan" ::phonelist ::arclist phonelen 1
 if {$::phonelist != {!NULL {sil <silence>} !NULL {Å: och} !NULL {Å och} {Å och} K k {sil <silence>} !NULL {asp <breath>} {sil <silence>} {asp <breath>} {sil <silence>} {O: onödan} N Ö: D d A N !NULL {sil <silence>} !NULL}} {
  puts $::phonelist
 }
 if {$::arclist != {{0 1} {1 2} {0 2} {2 3} {3 4} {2 5} {5 4} {2 6} {6 7} {7 8} {8 4} {4 9} {9 10} {4 11} {11 10} {4 12} {12 13} {13 14} {14 10} {4 10} {10 15} {15 16} {16 17} {17 18} {18 19} {19 20} {20 21} {21 22} {22 23} {23 24} {22 24}}} {
  puts $::arclist
 }


 set ::phonelist {}
 set ::arclist {}
 set phonelen 0
 CreateLists "onödan och" ::phonelist ::arclist phonelen 1
 if {$::phonelist != {!NULL {sil <silence>} !NULL {O: onödan} N Ö: D d A N !NULL {sil <silence>} !NULL {asp <breath>} {sil <silence>} {asp <breath>} {sil <silence>} !NULL {Å: och} !NULL {Å och} {Å och} K k !NULL {sil <silence>} !NULL}} {
  puts $::phonelist
 }
 if {$::arclist != {{0 1} {1 2} {0 2} {2 3} {3 4} {4 5} {5 6} {6 7} {7 8} {8 9} {9 10} {10 11} {11 12} {10 13} {13 12} {10 14} {14 15} {15 16} {16 12} {10 12} {12 17} {17 18} {18 19} {17 20} {20 19} {17 21} {21 22} {22 23} {23 19} {19 24} {24 25} {25 26} {24 26}}} {
  puts $::arclist
 }

}
#VerifyCreateLists ; exit

# Compute percentage of short (3 frames) segments

proc GetConfidence {res} {
#puts $res
 set short 0
 set tot 0
 set in 0
 for {set i 1} {$i < [llength $res]} {incr i} {
  set lab [lindex $res $i]
  if {[llength $lab] == 3} {
   set in 1
  }
  if {[string equal [lindex $lab 2] "<time>"] || \
	  [string equal [lindex $lab 2] "<speech>"] || \
	  [string equal [lindex $lab 2] "<silence>"]} {
   set in 0
  }
  set len [expr [lindex $lab 0] - [lindex [lindex $res [expr $i-1]] 0]]
  if {$in} {
   if {$len == 3} { incr short }
   incr tot
  }
 }
# puts "Percent short: [expr 100.0* $short / $tot] ($short/$tot)"
 expr 100.0 * $short / $tot
}

proc InsertEmptyTimeTags {text} {
 set res {<time begin="x.y" end="x.y">} 
 set pos 0
 while {1} {
#  regexp -indices -start $pos {\s+\S+\s+\S+\s+\S+\s+\S+\s+\S+\s+} $text index
  regexp -indices -start $pos {\s+\S+\s+\S+\s+} $text index
  if {[info exists index] == 0} break
  set pos [lindex $index end]
  regexp -indices -start $pos {[\.\!\?\;\:\)]\s+[A-ZÅÄÖ\-\"0-9\(]} $text index
  if {$pos == [lindex $index end]} break
  set pos [lindex $index end]
#  puts $index,[string range $text [lindex $index 0] [expr [lindex $index 0]+10]]
  set text [string replace $text [lindex $index 0] [lindex $index 0] \
		{</time><time begin="x.y" end="x.y">}]
  incr pos 33
 }
 append res $text
 append res {</time>}

 return $res
}

proc SaveRes {res1 timeLen} {
 set _f [open alab$::zzz.ord w]
 set _s ""
 foreach e $res1 {
  if {[llength $e] == 3} {
   if {$_s != ""} {
    puts $_f "$_s [expr [lindex $e 0]/100.0] $_l"
   }
   set _s [expr [lindex $e 0]/100.0]
   set _l [lindex $e 2]
  }
 }
 puts $_f "$_s [expr $_s+$timeLen] $_l"
 close $_f
 set _f [open alab$::zzz.lab w]
 set _s ""
 foreach e $res1 {
  if {$_s != ""} {
   puts $_f "$_s [expr [lindex $e 0]/100.0] $_l"
  }
  set _s [expr [lindex $e 0]/100.0]
  set _l [lindex $e 1]
 }
 puts $_f "$_s [expr $_s+$timeLen] $_l"
 close $_f
 incr ::zzz
}

proc AssertOverlap {bestSet reslistVar i j} {
 upvar #0 $reslistVar reslist
 
 foreach set $::ensemble($::lang) {
  set framelen $::framelenmap($set)
  set segres [lindex $reslist($set) $j]
  set stime [expr {[lindex [lindex $segres $i] 0]*$framelen}]
  set etime [expr {[lindex [lindex $segres [expr $i+1]] 0]*$framelen}]
  set a($set,start) $stime
  set a($set,end) $etime
 }
 set max 0
 set maxs ""
 foreach s1 $::ensemble($::lang) {
  set a($s1,overlap) {}
  foreach s2 $::ensemble($::lang) {
   if {$a($s1,start) <= $a($s2,end) && $a($s2,start) <= $a($s1,end)} {
    lappend a($s1,overlap) $s2
   }
  }
  if {[llength $a($s1,overlap)] > $max} {
   set max [llength $a($s1,overlap)]
   set maxs $s1
  }
 }
 # original set should be best guess
 if {[lsearch $a($maxs,overlap) $bestSet] > -1} { return $bestSet }
 
 set min 10
 foreach set $a($maxs,overlap) {
  if {abs($a($set,end)-$a($bestSet,end)) < $min} {
   set min [expr abs($a($set,end)-$a($bestSet,end))]
   set res $set
  }
 }
 if {[info exists res] == 0} { set res $bestSet }
 #puts $i,$min,${bestSet}->$res
 return $res
}


snack::sound s 
snack::sound param -encoding float -debug 0
#set monoAmpF [snack::filter map 65535.0 0.0 0.0 0.0]
set monoAmpF [snack::filter map 32767.0]

set files $inAudio

#source libalign.tcl
set useLexicon 0
if {$lexname != "" && [file readable $lexname]} {
 source $lexname
 set useLexicon 1
}
if {[info procs getHmmSets] == ""} {
# must be left adjusted
source acousticModel.tcl
}
set hmmsets [getHmmSets]
foreach pair $hmmsets settings [getParamSettings] {
 foreach {hset name} $pair {
  set hsets($name) $hset
  set params($name) $settings
 }
}
if {[info exists ensemble(se)] == 0} { set ensemble(se) "1016" }

# Check to see that all words occur in the lexicon
if {$realign == 0} {
 foreach file $files {
  if {[file readable $::inXML]} {
   #set f [open $inXML]
   #set text [read $f]
   #close $f
   
   set text [TextNorm $text 0]
   set doneLex 0
   foreach word [tokenise $text] {
    if {[regexp {[\w]+} $word]} {
     if {[info exists ::trans($word)] == 0} {
      if {$::debug} {	puts -nonewline "Lexicon look-up..." }
      CreateLexicon
      #unset lts::lts lts::ltss lts::ltsw n lts::ltsTree lts::ltstTree lts::ltsTreeN lts::ltstTreeN; #sparar typ 500MB
      #unset lts::lts lts::ltss lts::ltsw n lts::ltsTree lts::ltstTree ; #sparar typ 500MB
      # 071012 removed n, did not exist for english
      unset lts::lts lts::ltss lts::ltsw lts::ltsTree lts::ltstTree ; #sparar typ 500MB
      if {$::debug} {	puts "done" }
      if {$lexname != "" && $useLexicon == 0} {
       SaveLexicon $lexname
      }
      set doneLex 1
      break
     }
    }
   }
   if {$doneLex} break
  }
 }
}

if {$TFAname != ""} {
 set f1 [open $TFAname w]
 
 foreach ndirs [list 0 1 2 3] {
  
  if {$ndirs == [llength [file split [file dirname [lindex $files 0]]]]} {
   break
  }

  set path [eval file join [lrange [file split \
			[file dirname [lindex $files 0]]] 0 end-$ndirs]]
  set chopMore 0
  foreach f $files {
   if {[string match "$path*" $f] == 0} { set chopMore 1 ; break}
  }
  if {$chopMore == 0} break
 }
 set TFAcomponents $ndirs
}

set fileIndex 0

foreach file $files {
# snack::progressCallback "Processing files..." [expr double($fileIndex)/[llength $files]]
 incr fileIndex

 if {$::debug } { puts "Aligning: $file" }

 # Konvertera ljudfil till 16kHz

 if {[file readable $file]} {
  set soundfile $file
 } else {
  puts stderr "Could not open file: $file"
  exit -1
 }
 
 if {[file size $soundfile] < 25000000} {
  s read $soundfile
  if {[s cget -rate] < 16000} { puts "Warning, sample rate <16kHz: $file" }
  s convert -rate 16000
  if {[s cget -encoding] == "Float"} {
   s convert -encoding lin16
   s filter $monoAmpF
   #s convert -channels 1
  }
 } else {
  # big files:
  s config -file $soundfile
 }
 
 # Gör phonelist
 
 set root [file rootname $file]
 set name $::inXML
 if {[file readable $name] && $phoneRec == 0 && $realign == 0} {
  #set f [open $name]
  #set text [read $f]
  #close $f
  set createNet 1
  if {$grep != "" && [string match -nocase "*$grep*" $text] == 0} continue
  if {$::debug > 1} { puts "Reading $name" }
 } else {
  if {$phoneRec} {
   set phonelist [align::GetPhoneRecNoSilPhoneList $phones]
   set arclist   [align::GetPhoneRecNoSilArcList $phones]
   if {$::debug > 1} { puts "Performing phone recognition" }
  } elseif {$MLFname != ""} {
   set phonelist1 [align::getPronGraph1FromMLF $MLFname [file tail [file root $name]]$phoneFileExt]
   set phonelist {}
   foreach p $phonelist1 {
    lappend phonelist [tpa2hmm $p]
   }
   set arclist   [align::getPronGraph2FromMLF $phonelist]
   if {$::debug > 1} { puts "Realigning from $name in $MLFname" }
  } else {
   set phonelist1 [align::GetPhonesFromFile [file root $name]$phoneFileExt]
   set phonelist {}
   foreach p $phonelist1 {
    lappend phonelist "[tpa2hmm $p] \"$p\""
   }
   set arclist   [align::GetArcsFromFile [file root $name]$phoneFileExt]
   if {$arclist == ""} {
    puts stderr "Warning: only one segment in file, nothing to align ([file root $name]$phoneFileExt)"
    continue
   }
   if {$::debug > 1} { puts "Realigning from [file root $name]$phoneFileExt" }
  }
  set text ""
  set createNet 0
 }
 unset -nocomplain reslist
 set seg {}
 set chunklist {}
 set chunkends {}
 set startIndex 0
 while {[string first <fixpoint: $text $startIndex] != -1} {
  set endIndex [expr [string first <fixpoint: $text $startIndex] - 1]
  lappend chunklist [string range $text $startIndex $endIndex]
  if {[regexp {.*<fixpoint:(.+)>.*} [string range $text $endIndex [expr 20+$endIndex]] dummy fp]} {
   if {[lindex $chunkends end] == $fp} {
    puts "Error: duplicate fixpoint ($fp)"
    exit -1
   }
   lappend chunkends $fp
  }
  incr endIndex
  set startIndex [expr [string first > $text $endIndex] + 1]
 }
 lappend chunklist [string range $text $startIndex end]
 if {$endTime == -1} {
  lappend chunkends [s length -unit seconds]
 } else {
  if {$endTime > [s length -unit seconds]} {
   set endTime [s length -unit seconds]
  }
  lappend chunkends $endTime
 }
 if {$startTime > [s length -unit seconds]-.7 && [s length -unit seconds] > .7} {
  puts "Error: bad -start value: $startTime"
  exit -1
 } else {
  set starttime $startTime
 }
 if {1} {
  for {set i 0} {$i < [llength $chunklist]} {incr i} {
   set chunk   [lindex $chunklist $i]
   set endtime [lindex $chunkends $i]
   if {$createNet} {
    set isLastChunk [expr $i == [llength $chunklist]-1]

    set chunk [TextNorm $chunk $isLastChunk]
    if {$doTimeTags} {
     if {[regexp -all {<time[\w\s\"\.\=]*>} $text] == 0} {
      # Special for GROG
      if {[regexp -all {<I1>|<I2>} $text]} {
       regsub -all {<GS>|<GP>|<TB>|<I1>|<I2>} $text " <time> " chunk
      } else {
       #set chunk [InsertEmptyTimeTags $chunk]
      }
     }
    }
#puts $chunk
    set text $chunk
#puts QQ,$text
    CreateLists $text phonelist arclist phonelen 1 ;#$isLastChunk
#puts phonelist:$phonelist
   }
   if {$intro == "dump"} {
    puts $phonelist,$arclist
    exit
   }
   lappend seg $starttime
   lappend seg $endtime
   
   # Gör alignment på detta segment (oftast samma som hela filen)
   
   if {$useHMMset != ""} {
    set hsetlist $useHMMset
   } else {
    set hsetlist $ensemble($lang)
   }

   for {set j 0} {$j < [llength $hsetlist]} {incr j} {
    set name [lindex $hsetlist $j]
    set hset $hsets($name)
    foreach {framelen windowlen energy zeromean} $params($name) break
    set framelenmap($name) $framelen
    s speatures param -energy $energy -framelength $framelen \
	-windowlength $windowlen -zeromean $zeromean
    if {$debug > 2} {  puts "\{$phonelist\} \{$arclist\}" }
#         puts [lrange $phonelist 0 15],[lrange $arclist 0 10]    
    #    puts "param an $phonelist $arclist $hset -start [expr int($starttime/$framelen+.5)] -end [expr int($endtime/$framelen+.5)] -output phones"
    #   puts [expr int(($endtime-$starttime)/$framelen)],[llength $phonelist]
    if $memdebug {
     memory validate on
     #memory trace on
    }
    if {[catch {set res [param an $phonelist $arclist $hset \
		 -start [expr int($starttime/$framelen+.5)] \
		 -end [expr int($endtime/$framelen+.5)] -output phones \
			     -prunethreshold $prunet -backtrack $backtrack]} err]} {
     puts stderr $err
     exit -1
   }
    if $memdebug {
     #memory trace off
    }
#         puts [lrange $res 0 5]   
#exit
#    puts $res
    lappend reslist($name) $res
    if {$::beQuick} break
    if {$j == 0} {
     set nPhones [llength $res]
     set phonelist {}
     set arclist {}
     set k 0
     # tankte gora kod som tog bort korta segment, fyra narmaste bortkommenterade raderna, ta bort?
     #     set oldframe -4
     for {set l 0} {$l < $nPhones - 1} {incr l} {
      #      set frame [lindex [lindex $res $l] 0]
      #      if {[lindex [lindex $res $l] 1] == "sil"} { puts [expr $frame - $oldframe] }
      if {[llength [lindex $res $l]] == 3} {
       lappend phonelist [list [lindex [lindex $res $l] 1] \
			      [lindex [lindex $res $l] 2]]
      } else {
       lappend phonelist [lindex [lindex $res $l] 1]
      }
      if {$l < $nPhones - 2} {
       lappend arclist [list $k [incr k]]
      }
      #      set oldframe $frame
     }
    }
   }
   set starttime $endtime
  }
 } ;# if 1

 # create transcription list by joining the lists for each chunk
 # in $reslist

 set l1 {}
 set l2 {}
 if {$TFAname != ""} {
  if {$doWords} {
   set f2 $f1
  }
 } else {
  if {$doPhones} {
   set f1 [open $root$phoneFileExt w]
  }
  if {$doWords} {
   set f2 [open $root$wordFileExt w]
  }
 }
 set segli 0

 if {$useMethod == "default"} {
  set name [lindex $hsetlist 0]
  for {set j 0} {$j < [llength $reslist($name)]} {incr j} {
   set segres [lindex $reslist($name) $j]
   set start  0; #[lindex $seg $segli]
   set wrd ""
#puts $segres
   if {[string match {*<silence>} [lindex $segres end-1]] == 0} {
    set endFix 1
   } else {
    set endFix 0
   }
   for {set i 0} {$i < [llength $segres]-2} {incr i} {
    if {$::beQuick == 0} {
     # determine phone class and let the best hmm set from the ensemble decide
     set cp  [lindex [lindex $segres $i] 1]
     set np  [lindex [lindex $segres [expr $i+1]] 1]
     set best $::ensemble($::class($cp),$::class($np))
    } else {
     set best $name
    }
    set framelen $framelenmap($best)
    set segres [lindex $reslist($best) $j]
    set labentry [lindex $segres $i]
#puts LABENTRY:$labentry,,,,,$endFix,,,,,[lindex $segres [expr $i+1]]
    if {$phoneRec == 0} {
     set phn [lindex [split [lindex $labentry 2]] 0]
    } else {
     set phn [lindex $labentry 1]
    }
    if {$i == 0} {
     set ostime [expr {[lindex [lindex $segres $i] 0]*$framelen}]
    }
    set stime $ostime
    set etime [expr {[lindex [lindex $segres [expr $i+1]] 0]*$framelen}]
    
    
    if {$useHMMset == "" && $beQuick == 0} {
     set segres525 [lindex $reslist(525) $j]
     if {$stime < [lindex [lindex $segres525 [expr $i+1]] 0]*$framelenmap(525) ||\
	     $etime > [lindex [lindex $segres525 $i] 0] * $framelenmap(525)} {
      #       puts "no overlap!"
     }
    }
    
    
    if {$stime >= $etime && $beQuick == 0} {
     if {$::debug} {	
      puts "Patching inconsistent boundary $phn,$stime,$etime"
     }
     set nstime [format "%.7f" [expr $etime - 0.003]]
     set last [lindex $l1 end]
#puts AA,$last
     set l1 [lreplace $l1 end end [lreplace $last 1 1 $nstime]]
     set last [lindex $l2 end]
     if {[lindex $last 1] == [format "%.7f" $stime]} {
      set l2 [lreplace $l2 end end [lreplace $last 1 1 $nstime]]
     }
     set stime $nstime
    }
    
    set ostime $etime
    if {$doZeroX} {
     set stime [AdjustZeroX s $stime]
     set etime [AdjustZeroX s $etime]
    }
    if {[string match {* *} [lindex $labentry 2]] && \
	    [regexp {.*_(\d)} [lindex $labentry 2] dummy stress]} {
#puts B$labentry,$phn
     append phn _$stress
    }
    if {$phn == ""} { set phn sil }
#puts C,$phn
    lappend l1 "[format "%.7f" [expr $stime+$start]] [format "%.7f" [expr $etime+$start]] $phn"
    
    if {[string match {* *} [lindex $labentry 2]]} {
     set wrd [lindex [lindex $labentry 2] end]
#puts BBB$labentry,$wrd
     #	set wrdt [expr {[lindex $labentry 0]*$framelen}]
     set wrdt $stime
     if {$doZeroX} {
      set wrdt [AdjustZeroX s $wrdt]
     }
    }
#puts QQQQ,,,[lindex [lindex $segres [expr $i+1]] end],[string match {* *} [lindex [lindex $segres [expr $i+1]] end]]
if {[string match {* *} [lindex [lindex $segres [expr $i+1]] end]]} {
     lappend l2 "[format "%.7f" [expr $wrdt+$start]] [format "%.7f" [expr $etime+$start]] $wrd"
    }
   }
   # sista elementet i $segres
   set phn [lindex [lindex [lindex $segres $i] 2] 0]
   if {$endFix == 0 || [lindex [lindex $segres $i] 2] != ""} {
    if {[string match {* *} [lindex [lindex $segres $i] end]]} {
     set wrd [lindex [lindex [lindex $segres $i] 2] end]
    }
   }
   incr segli
   if {[llength [lindex $segres $i]] == 3 && \
	   [regexp {.*_(\d)} [lindex [lindex $segres $i] 2] dummy stress]} {
    append phn _$stress
   }

   lappend l1 "[format "%.7f" [expr $etime+$start]] [format "%.7f" [lindex $seg $segli]] $phn"
   lappend l2 "[format "%.7f" [expr $etime+$start]] [format "%.7f" [lindex $seg $segli]] $wrd"
   incr segli
  } ;# end foreach segres

 } elseif {[string equal [string tolower $useMethod] "bem"]} { ;# method BEM

  for {set j 0} {$j < [llength $reslist($name)]} {incr j} {
   set segres [lindex $reslist($name) $j]
   set start  0 ; #[lindex $seg $segli]
   set wrd ""
   for {set i 0} {$i < [llength $segres]-2} {incr i} {
    if {$i == 0} {
     set ostime 0
    }
    set etime 0
    foreach e $hmmsets {
     foreach {hset name} $e break
     set framelen $framelenmap($name)
     set segres [lindex $reslist($name) $j]
     if {$i == 0} {
      set ostime [expr {$ostime + [lindex [lindex $segres $i] 0]*$framelen}]
     }
     set etime [expr {$etime +[lindex [lindex $segres [expr $i+1]] 0]*$framelen}]
    }
    set etime  [expr {$etime/[llength $hmmsets]}]
    set labentry [lindex $segres $i]
    set phn [lindex $labentry 1]
    set stime $ostime
#    puts $stime,$etime,$phn
    if {$stime >= $etime} {
     if {$::debug} {	
      puts "Patching inconsistent boundary $phn,$stime,$etime"
     }
     set nstime [format "%.7f" [expr $etime - 0.003]]
     set last [lindex $l1 end]
     set l1 [lreplace $l1 end end [lreplace $last 1 1 $nstime]]
     set last [lindex $l2 end]
     if {[lindex $last 1] == [format "%.7f" $stime]} {
      set l2 [lreplace $l2 end end [lreplace $last 1 1 $nstime]]
     }
     set stime $nstime
    }
    
    set ostime $etime
    if {$doZeroX} {
     set stime [AdjustZeroX s $stime]
     set etime [AdjustZeroX s $etime]
    }
    if {[llength $labentry] == 3 && \
	    [regexp {.*_(\d)} [lindex $labentry 2] dummy stress]} {
     append phn _$stress
    }
    lappend l1 "[format "%.7f" [expr $stime+$start]] [format "%.7f" [expr $etime+$start]] $phn"
    
    if {[llength $labentry] == 3 && [string match _? [lindex $labentry 2]] == 0} {
     set wrd [lindex $labentry 2]
     set wrdt $stime
     if {$doZeroX} {
      set wrdt [AdjustZeroX s $wrdt]
     }
    }
    if {[llength [lindex $segres [expr $i+1]]] == 3 && \
	    [string match _? [lindex [lindex $segres [expr $i+1]] 2]] == 0} {
     lappend l2 "[format "%.7f" [expr $wrdt+$start]] [format "%.7f" [expr $etime+$start]] $wrd"
    }
   }
   set phn [lindex [lindex $segres $i] 1]
   set wrd [lindex [lindex $segres $i] 2]
   incr segli
   lappend l1 "[format "%.7f" [expr $etime+$start]] [format "%.7f" [lindex $seg $segli]] $phn"
   lappend l2 "[format "%.7f" [expr $etime+$start]] [format "%.7f" [lindex $seg $segli]] $wrd"
   incr segli
  } ;# end foreach segres




 } elseif {[string equal [string tolower $useMethod] "cart"]} { ;# method CART
  for {set j 0} {$j < [llength $reslist($name)]} {incr j} {
   set segres [lindex $reslist($name) $j]
   set start  0 ; #[lindex $seg $segli]
   set wrd ""
   if {[string match {*<silence>} [lindex $segres end-1]] == 0} {
    set endFix 1
   } else {
    set endFix 0
   }

   for {set i 0} {$i < [llength $segres]-2} {incr i} {
    
    # determine phone transition and let best hmm set from the ensemble decide
    set lab2  [lindex [lindex $segres $i] 1]
    set lab3  [lindex [lindex $segres [expr $i+1]] 1]
    if {$i > 0} {
     set lab1 [lindex [lindex $segres [expr $i-1]] 1]
    } else {
     set lab1 sil
    }
    if {$i < [llength $segres] - 2} {
     set lab4 [lindex [lindex $segres [expr $i+2]] 1]
    } else {
     set lab4 sil
    }
    set featVect [list $lab2 $lab3]
    set bestSet [walkTree $tree($lang)] ;# walk CART-tree
    set bestSet [AssertOverlap $bestSet reslist $i $j]
    if {1&&[info exists offset($lang)]} {
     set meanDev [walkTree $offset($lang)]
    } else {
     set meanDev 0.0
    }
    set framelen $framelenmap($bestSet)
    set segres [lindex $reslist($bestSet) $j]
    set labentry [lindex $segres $i]
    set phn [lindex [split [lindex $labentry 2]] 0]
    if {$i == 0} {
     set ostime [expr {[lindex [lindex $segres $i] 0]*$framelen}]
    }
    set stime $ostime
    set etime [expr {[lindex [lindex $segres [expr $i+1]] 0]*$framelen}]
    set etime [expr {$etime+$meanDev}]

    # this seems like a good place to mess around with the time boundary (etime)

    if {0 && [lsearch {P T K RT sil pcl tcl kcl pau} $lab3] >= 0 && \
	    $i < [llength $segres] - 3} {
     set pwrList [s power]
     #     puts "$lab2 $lab3 $etime,[lindex $pwrList [expr int($etime*100+.5)]]"
     set startIndex [expr int($etime*100+.5)]
     set sta1 [expr {[lindex [lindex $segres [expr $i+1]] 0]*$framelen}]
     set end1 [expr {[lindex [lindex $segres [expr $i+2]] 0]*$framelen}]
     set tresh [expr 1.4*[lindex $pwrList [expr int(($sta1+($end1-$sta1)/2)*100+.5)]]]
     for {set index 0} {$index < 5} {incr index} {
      if {[lindex $pwrList [expr $startIndex + $index]] < $tresh} break
     }
     if {$index < 5} {
      set etime [expr 0.01*($startIndex + $index)]
     }
     #     puts "New $etime"
    }

    if {0 && [lsearch {F S f s} $lab3] >= 0 && $i < [llength $segres] - 3} {
     set pitList [s pitch -method ESPS]
     set startIndex [expr int($etime*100+.5)]
     for {set index 0} {$index < 5} {incr index} {
      if {[lindex [lindex $pitList [expr $startIndex + $index]] 0] == 0.0} break
     }
     if {$index < 5} {
      set etime [expr 0.01*($startIndex + $index)]
     }
    }


    # Repair error if any
    if {$stime >= $etime} {
     if {$::debug} {	
      puts "Patching inconsistent boundary $phn,$stime,$etime"
     }
     set nstime [format "%.7f" [expr $etime - 0.003]]
     if {[lindex [lindex $l1 end] 0] > $nstime} {
      set nstime [format "%.7f" [expr $stime + 0.003]]
      set etime [format "%.7f" [expr $nstime + 0.003]]
     }
     set l1 [lreplace $l1 end end [lreplace [lindex $l1 end] 1 1 $nstime]]
     if {[lindex [lindex $l2 end] 1] == [format "%.7f" $stime]} {
      set l2 [lreplace $l2 end end [lreplace [lindex $l2 end] 1 1 $nstime]]
     }
     set stime $nstime
    }
    
    if {$doZeroX} {
     # gammal kod, helt klart inte optimal
          set stime [AdjustZeroX s $stime]
          set etime [AdjustZeroX s $etime]
#     set etime [AdjustZeroX2 s $etime]
    }
    set ostime $etime
    if {[string match {* *} [lindex $labentry 2]] && \
	    [regexp {.*_(\d)} [lindex $labentry 2] dummy stress]} {
     append phn _$stress
    }
    lappend l1 "[format "%.7f" [expr $stime+$start]] [format "%.7f" [expr $etime+$start]] $phn"
    
    if {[string match {* *} [lindex $labentry 2]]} {
     set wrd [lindex [lindex $labentry 2] end]
     set wrdt $stime
     if {$doZeroX} {
      set wrdt [AdjustZeroX s $wrdt]
     }
    }
    if {[string match {* *} [lindex [lindex $segres [expr $i+1]] end]]} {
     lappend l2 "[format "%.7f" [expr $wrdt+$start]] [format "%.7f" [expr $etime+$start]] $wrd"
    }
   }
   set phn [lindex [lindex [lindex $segres $i] 2] 0]
   if {$endFix == 0 || [lindex [lindex $segres $i] 2] != ""} {
    if {[string match {* *} [lindex [lindex $segres $i] end]]} {
     set wrd [lindex [lindex [lindex $segres $i] 2] end]
    }
   }

   incr segli
   if {[llength [lindex $segres $i]] == 3 && \
	   [regexp {.*_(\d)} [lindex [lindex $segres $i] 2] dummy stress]} {
    append phn _$stress
   }
   lappend l1 "[format "%.7f" [expr $etime+$start]] [format "%.7f" [lindex $seg $segli]] $phn"
   lappend l2 "[format "%.7f" [expr $etime+$start]] [format "%.7f" [lindex $seg $segli]] $wrd"
   incr segli
  }
 } else {
  puts "Error unknown method"
  exit
 }

 # write out l1 and l2 to file

# puts $l2 
 if {$doPhones} {
  if {$TFAname != ""} {
   set name [eval file join [lrange [file split $root] end-$TFAcomponents end]]
   puts $f1 "\"*/${name}$phoneFileExt\""
  }
  if {$doStress == 0} {
   for {set i 0} {$i < [llength $l1]-1} {incr i} {
    set thisend [lindex [lindex $l1 $i] 1]
    set nextstart [lindex [lindex $l1 [expr $i+1]] 0]
    if {$::debug && $thisend != $nextstart} {puts "Inconsistent boundary1, $file, $thisend, $nextstart"}
    set thislab [lindex [lindex $l1 $i] 2]
    set nextlab [lindex [lindex $l1 [expr $i+1]] 2]
    if {$thislab == "sil" && $nextlab == "sil"} {
     puts $f1 [lreplace [lindex $l1 [expr $i+1]] 0 0 [lindex [lindex $l1 $i] 0]]
     incr i
     continue
    }
    puts $f1 [lindex $l1 $i]
   }
   if {$i < [llength $l1]} {
    puts $f1 [lindex $l1 end]
   }
  } else {
   for {set i 0} {$i < [llength $l1]-1} {incr i} {
    set thisend [lindex [lindex $l1 $i] 1]
    set nextstart [lindex [lindex $l1 [expr $i+1]] 0]
    if {$::debug && $thisend != $nextstart} {puts "Inconsistent boundary2, $file, $nextstart"}
    set thislab [lindex [lindex $l1 $i] 2]
    set nextlab [lindex [lindex $l1 [expr $i+1]] 2]
    if {$thislab == "sil" && $nextlab == "sil"} {
     puts $f1 [lreplace [lindex $l1 [expr $i+1]] 0 0 [lindex [lindex $l1 $i] 0]]
     incr i
     continue
    }
    set stressphone [lindex $l1 $i]
    regsub -all {(\S+)_1} $stressphone {'\1} stressphone
    regsub -all {(\S+)_2} $stressphone {"\1} stressphone ;#"\} keep emacs happy
    regsub -all {(\S+)_3} $stressphone {`\1} stressphone
    puts $f1 $stressphone
   }
   if {$i < [llength $l1]} {
    set stressphone [lindex $l1 end]
    regsub -all {(\S+)_1} $stressphone {'\1} stressphone
    regsub -all {(\S+)_2} $stressphone {"\1} stressphone ;#"\} keep emacs happy
    regsub -all {(\S+)_3} $stressphone {`\1} stressphone
    puts $f1 $stressphone
   }
  }
 }

 if {$doWords} {
  if {$TFAname != ""} {
   set name [eval file join [lrange [file split $root] end-$TFAcomponents end]]
   puts $f2 "\"*/${name}$wordFileExt\""
  }
  for {set i 0} {$i < [llength $l2]-1} {incr i} {
   set thisend [lindex [lindex $l2 $i] 1]
   set nextstart [lindex [lindex $l2 [expr $i+1]] 0]
   if {$::debug && $thisend != $nextstart} {puts "Inconsistent boundary3, $file, $thisend, $nextstart"}
   set thislab [lindex [lindex $l2 $i] 2]
   set nextlab [lindex [lindex $l2 [expr $i+1]] 2]
   if {$thislab == "<silence>" && $nextlab == "<silence>"} {
    puts $f2 [lreplace [lindex $l2 [expr $i+1]] 0 0 [lindex [lindex $l2 $i] 0]]
    incr i
    continue
   }
   if {$doStress} {
    if {[regexp {.+_[123]$} [lindex $l2 $i]]} {
     set word [lindex [split [lindex $l2 $i] _] 0]
    } else {
     set word [lindex $l2 $i]
    }
   } else {
    set word [lindex $l2 $i]
   }
   puts $f2 $word
  }
  if {$i < [llength $l2]} {
   if {$doStress} {
    if {[regexp {.+_[123]$} [lindex $l2 end]]} {
     puts $f2 [lindex [split [lindex $l2 end] _] 0]
    } else {
     puts $f2 [lindex $l2 end]
    }
   }
  }
 }

 if {$doTimeTags} {

  if 0 {
   set name $::inXML
   set f3 [open $name]
   set text [read -nonewline $f3]
   close $f3

   if {[regexp -all {<time[\w\s\"\.\=]*>} $text] == 0} {
    set text [InsertEmptyTimeTags $text]
   }
  }
  #puts $l2
  set f4 [open $outXML w]
  fconfigure $f4 -encoding utf-8

  # split sil in <time> in the middle, too complex now, after other changes
  set tmp {}
  for {set i 0} {$i < [llength $l2]-1} {incr i} {
   set thisstart [format "%.2f" [lindex [lindex $l2 $i] 0]]
   set thisend   [format "%.2f" [lindex [lindex $l2 $i] 1]]
   set thislab   [lindex [lindex $l2 $i] 2]
   if {$i < [llength $l2]-2} {
    set nextlab   [lindex [lindex $l2 [expr $i+1]] 2]
   }
   if {$thislab == "<time>" || $thislab == "<speech>"} {
    set first 1
    for {set j 0} {$j < [llength $l1]-1} {incr j} {
     set fonstart [format "%.2f" [lindex [lindex $l1 $j] 0]]
     set fonend   [format "%.2f" [lindex [lindex $l1 $j] 1]]
     set fonlab   [lindex [lindex $l1 $j] 2]
     if {$fonstart >= $thisstart && $fonend <= $thisend && \
	     ($fonlab =="sil" || $fonlab =="asp" || $fonlab =="all")} {
      if {$nextlab == "<time>"} {
       lappend tmp y.x
       break
      }
      set thismid  [expr $fonstart + ($fonend - $fonstart) / 2]
      set goodEnd $fonend
      # add midpoint of first sil-segment in <time>
      if {$first} {
       lappend tmp $fonstart
      }
      set first 0
     }
    }
    # add midpoint of last sil-segment in <time>
    if {$first == 0} {
     lappend tmp $goodEnd
    }
   }
  }
  set posl [lsearch -all $tmp y.x]
  if {$posl != {}} {
   foreach pos $posl {
    set tmp [lreplace $tmp $pos $pos]
    incr pos
    set tmp [linsert $tmp $pos y.x y.x]
   }
  }
  set tmp [lrange $tmp 1 end]
#  puts $tmp
  set endPoint [format "%.2f" [s length -unit seconds]]
  for {set j [expr [llength $l1]-1]} {$j > 0} {incr j -1} {
   set fonend   [format "%.2f" [lindex [lindex $l1 $j] 1]]
   set fonlab   [lindex [lindex $l1 $j] 2]
   if {($fonlab =="sil" || $fonlab =="asp" || $fonlab =="all")} {
    continue
   }
   set endPoint $fonend
   break
  }
  lappend tmp $endPoint
#puts QQQ,$tmp
# ska bort
#regsub -all {y.x } $tmp "" tmp
#puts QQQ,$tmp
  if 0 {
   # Add little silence before and after each timetagged chunk
   set new {}
   if {[lindex $tmp 0] > 0.1} {
    lappend new [expr [lindex $tmp 0] - 0.1]
   } else {
    lappend new 0.0
   }
   foreach {a b} [lrange $tmp 1 end-1] {
    if {$a + 0.2 < $b} {
     lappend new [expr $a + 0.1]
     lappend new [expr $b - 0.1]
    } else {
     lappend new [expr $a + ($b - $a) / 2]
     lappend new [expr $b - ($b - $a) / 2]
    }
   }
   if {[lindex $tmp end] + 0.1 < [s length -unit seconds]} {
    lappend new [expr [lindex $tmp end] + 0.1]
   } else {
    lappend new [s length -unit seconds]
   }
   set tmp $new
  }  
  if 0 {
   foreach {stamp1 stamp2} $tmp {
    regsub {x\.y} $text $stamp1 text
    regsub {x\.y} $text $stamp2 text
   }
   regsub -all {y\.x} $text x.y text
  }
#puts AAA,$tmp
# lappend tmp 0 0 0 0 0 0 0 0
  set text ""
  set parserMode insertTime
  set tmpIndex 0
  $parser parse $data

  puts $f4 $text
  close $f4
 }

 #  foreach e $l1 { puts $f1 $e }
 #  foreach e $l2 { puts $f2 $e }
 if {$TFAname == ""} {
  if {$doPhones} {
   close $f1
  }
  if {$doWords} {
   close $f2
  }
 }
 if {$cropMargin > -1} {
  set cropStart -1
  for {set i 0} {$i < [llength $l2]-1} {incr i} {
   set thisstart [lindex [lindex $l2 $i] 0]
   set thislab [lindex [lindex $l2 $i] 2]
   if {[string match "<*>" $thislab] == 0} {
    set cropStart [expr $thisstart - $cropMargin]
    break
   }
  }
  set cropEnd -1
  for {set i [expr [llength $l2] - 1]} {$i >= 0} {incr i -1} {
   set thisend [lindex [lindex $l2 $i] 1]
   set thislab [lindex [lindex $l2 $i] 2]
   if {[string match "<*>" $thislab] == 0} {
    set cropEnd [expr $thisend + $cropMargin]
    break
   }
  }
  s write [file rootname $soundfile]_o[file extension $soundfile]
  s write $soundfile \
      -start [expr int($cropStart * [s cget -rate])] \
      -end   [expr int($cropEnd * [s cget -rate])]
 }
}
if {$TFAname != ""} {
 close $f1
}

if {$::tcl_platform(platform) == "windows"} {
# console show
}

exit
