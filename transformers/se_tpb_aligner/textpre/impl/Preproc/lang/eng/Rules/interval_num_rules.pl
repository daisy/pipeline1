#!/usr/bin/perl -w


#**************************************************************#
sub interval_num_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;


	&interval_num_1();
	&interval_num_2();
	
	&id_num_1();
	&id_num_2();

}
#**************************************************************#
# interval_num_1
#
# ID number
# Example:	3214-987-5-8875
#		
#**************************************************************#
sub interval_num_1 {

	if ( exists ( $ort{ $next_4 } ) ) {
		if (
			# Context
			$ort{ $curr }		=~	/^\d+$/
			&&
			$ort{ $next_1 }		eq	"-"
			&&
			$ort{ $next_2 }		=~	/^\d+$/
			&&
			$ort{ $next_3 }		eq	"-"
			&&
			$ort{ $next_4 }		=~	/^\d+$/
		) {
			# Retag
			$type{ $curr }		=	"ID NUM";
			$type{ $next_1 }	=	"ID NUM";
			$type{ $next_2 }	=	"ID NUM";
			$type{ $next_3 }	=	"ID NUM";
			$type{ $next_4 }	=	"ID NUM";
			
			$pos{ $curr }		=	"NUM CARD";
			$pos{ $next_2 }		=	"NUM CARD";
			$pos{ $next_4 }		=	"NUM CARD";
			
			
			if ( exists ( $ort{ $next_6 } ) ) {
				if (
					$ort{ $next_5 }		eq	"-"
					&&
					$ort{ $next_6 }		=~	/^\d+$/
				) {
					$type{ $next_5 }	=	"ID NUM";
					$type{ $next_6 }	=	"ID NUM";

					$pos{ $next_6 }		=	"NUM CARD";
				}
			}
			
		}
		
		
	} # end exists
}
#**************************************************************#
# interval_num_2
#
# Intervals with fractions
# Example:		3-3½	3½-4½
#				
#		
#**************************************************************#
sub interval_num_2 {
	
	
	# With blanks
	if ( exists ( $ort{ $prev_2 } ) && exists ($ort{ $next_2 } )) {
		
#		print "\n
#		-----------
#		interval_num_2
#		Prev2: $type{ $prev_2 }
#		Prev1: $type{ $prev_1 }
#		Curr:  $ort{ $curr }	
#		Next1: $type{ $next_1 }
#		Next2: $type{ $next_2 }
#		\n";
		
		if (
			# Context
			$ort{ $curr }		eq	"-"
			&&
			(
				$type{ $prev_2 }	=~	/^NUM/
				||
				$pos{ $prev_2 }		=~	/NUM/
			)
			&&
			$pos{ $prev_1 }		eq	"DEL"
			&&
			$pos{ $next_1 }		eq	"DEL"
			&&
			(
				$type{ $next_2 }	=~	/^NUM/
				||
				$pos{ $next_2 }		=~	/NUM/
			)
		) {
			# Retag
			$type{ $curr }		=	"INTERVAL";
			
			$exp{ $curr }		=	"to";
		}
	} # end exists

	#---------------------------------#
	# Without blanks
	if ( exists ( $ort{ $prev_1 } ) && exists ($ort{ $next_1 } )) {
		
#		print "\n
#		-----------
#		interval_num_2
#		Prev1: $type{ $prev_1 }
#		Curr:  $ort{ $curr }	
#		Next1: $type{ $next_1 }
#		\n";
		
		if (
			# Context
			$ort{ $curr }		eq	"-"
			&&
			(
				$type{ $prev_1 }	=~	/^NUM/
				||
				$pos{ $prev_1 }		=~	/NUM/
			)
			&&
			(
				$type{ $next_1 }	=~	/^NUM/
				||
				$pos{ $next_1 }		=~	/NUM/
			)
		) {
			# Retag
			$type{ $curr }		=	"INTERVAL";
			
			$exp{ $curr }		=	"to";
		}
	} # end exists

}
#**************************************************************#
# id_num_1
#
# ID number
# Example:	3214-987-5-887-X
#		
#**************************************************************#
sub id_num_1 {

	if ( exists ( $ort{ $next_4 } ) ) {
		if (
			$ort{ $next_1 }		eq	"-"
			&&
			$ort{ $next_3 }		eq	"-"
			&&
			$ort{ $next_4 }		=~	/[a-zåäö\d]+/
		) {
			
			$type{ $curr }		=	"ID NUM";
			$type{ $next_1 }	=	"ID NUM";
			$type{ $next_2 }	=	"ID NUM";
			$type{ $next_3 }	=	"ID NUM";
			$type{ $next_4 }	=	"ID NUM";
		}
	} # end exists
}
#**************************************************************#
# id_num_2
#
# 0375-08X
#
#**************************************************************#
sub id_num_2 {
	
	if ( exists ( $ort{ $next_2 } ) ) {
		if (
			$ort{ $next_1 }		eq	"-"
			&&
			$type{ $next_2 }	=~	/MIXED/
		) {
			$type{ $curr }		=	"ID NUM";
			$type{ $next_1 }	=	"ID NUM";
			$type{ $next_2 }	=	"ID NUM";
		}			
	} # end exists
}	
#**************************************************************#

1;
#**************************************************************#


#		#-------------------------------------------------------#
#		# FORMULAS 						#
#		# 061128 Tar bort "/" från []-mängden.			#
#		#-------------------------------------------------------#
#		# X -+*/ Y = Z
#		} elsif ($tc =~ /<NUM>(.*?)( *[\+\-\*\=] *(.*?))+<\/NUM>/) {
#		
#				
#
#
#
#			# Om siffran före bindestrecket är mindre än den efter, ändra till intervall.
#			} elsif ($tc =~ /(\d+) *- *(\d+)/) {
#				my $first = $1;
#				my $second = $2;
#	
#
#				# 070105 Andra talet börjar med "0".
#				if ($second =~ /^0\d/) {
#					
#					$tc =~ s/(\d+) *- *(\d+)/<ID_NUM>$1 <CHAR>- exp\=streck<\/CHAR> $2/<\/ID_NUM>/;
#
#				# Äkta matematik
#				} else {
#
#					# Tagga matematiska symboler med <MATH>			
#					# 070122 Lägger till "-".
#					$tc =~ s/([\+\*\/\=\)\(\,\.\-])/<MATH>$1<\/MATH>/g;
#		
#					# Tagga hela uttrycket med <FORMULA>
#					$tc =~ s/^(.+)$/<FORMULA>$1<\/FORMULA>/;
#
#			} # end if $first < $second
#	
#			
#			
#			# Äkta matematik
#			} else {				
#			
#				# 1960-, 1970- och 1980-talet.		070213
#				if ($tc =~ s/(\d+)(-,)/$1<DONE>$2<\/DONE>/g) {
#
#				# 070211	^-300
#				} elsif ($tc =~ s/^-(\d)/<MATH>-<\/MATH>$1/g) {
#					$tc =~ s/(^.+)$/<FORMULA>$1<\/FORMULA>/;
#			
#				# Tagga matematiska symboler med <MATH>			
#				# 070104 Tar bort "-".
#				} elsif ($tc =~ s/([\+\*\/\=\)\(\,\.])/<MATH>$1<\/MATH>/g) {
#	
#					# Tagga hela uttrycket med <FORMULA>
#					$tc =~ s/^(.+)$/<FORMULA>$1<\/FORMULA>/;
#				
#				} else {
#					$tc =~ s/-/<CHAR>- exp\=streck<\/CHAR>/g;
#				}
#
#				# print "III $tc\n\n";
#				
#				
#
#
#			} # end if $tc =~
#			
#			
#			
#			# Tagga siffrorna med <NUM>
#			$tc =~ s/(\d+)/<NUM>$1<\/NUM>/g;
#			
#
#	
#				
#							
#		} # end if $tc
#				
#		$tc_counter++;
#	
#	} # end foreach $tc
#
#
#	$text = join"",@text;
#
#	# Om det inte var en formel eller ett id-nummer, kolla intervaller.
#	if ($text !~ /<(?:FORMULA|ID_NUM|TIME|DATE|INTERVAL)>/) {
#
#		
#		#-------------------------------------------------------#
#		# INTERVALS 						#
#		# 061128 Ändrat .*? till [^<]+
#		#-------------------------------------------------------#
#		# X % - Y % --> X % till Y %
#		$text =~ s/<NUM>([^<]+)<\/NUM> *(<UNIT>[^<]+<\/UNIT>) *- *<NUM>([^<]+)<\/NUM> *(<UNIT>[^<]+<\/UNIT>)/<INTERVAL><NUM_UNIT>$1<\/NUM_UNIT>$2<CHAR>- exp\=till<\/CHAR><NUM_UNIT>$3<\/NUM_UNIT>$4<\/INTERVAL>/g;
#
#		# X - Y % --> X till Y %
#		$text =~ s/<NUM>([^<]+)<\/NUM> *- *<NUM>([^<]+)<\/NUM> *(<UNIT>[^<]+<\/UNIT>)/<INTERVAL><NUM_UNIT>$1<\/NUM_UNIT><CHAR>till exp\=till<\/CHAR><NUM_UNIT>$2<\/NUM_UNIT>$3<\/INTERVAL>/g;
#		
#		# X - Y --> X till Y
#		$text =~ s/<\/(NUM(?:_....)?)> *- *<\/NUM(?:_....)?>/<INTERVAL><$1><CHAR>- exp\=till<\/CHAR><$1><\/INTERVAL>/ig;
#		
#		# Intervall med snedstreck (070111)
#		# Läs snedstreck som "snedstreck".
#		# 070211 En högerparentes kan finnas med.
#		$text =~ s/<NUM> *([12]\d\d\d) *\/ *(\d\d(?:\d\d))( *\)? *)<\/NUM>/<INTERVAL><NUM_YEAR>$1<\/NUM_YEAR><CHAR>\/ exp\=snedstreck<\/CHAR><NUM_YEAR>$2<\/NUM_YEAR><INTERVAL>$3/ig;
#		
#		# print "IO $text\n\n";
#		
#		# Årtalsintervall (061127)
#		# <NUM_YEAR>X-Y<\/NUM_YEAR>  --> X till Y
#		$text =~ s/<NUM_YEAR> *(\d\d\d\d) *- *(\d\d(?:\d\d)?) *<\/NUM_YEAR>/<INTERVAL><NUM_YEAR>$1<\/NUM_YEAR><CHAR>- exp\=till<\/CHAR><NUM_YEAR>$2<\/NUM_YEAR>/ig;
#
#
#	}
#
#	# print "\n\nret $text\n\n\n";
#
#	return $text;
#
#}


#		# 070105 Fulfix för Platon 427-346 f.Kr.
#		if ($text[$tc_counter+1] =~ /f\.? *kr\b/i) {
#			$year_flag = 1;
#			
#		}
