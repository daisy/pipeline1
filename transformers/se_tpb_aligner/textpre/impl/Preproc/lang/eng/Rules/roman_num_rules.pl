#!/usr/bin/perl -w


#**************************************************************#
sub roman_num_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;


	# Don't apply rules if type is already set.
	if ($type{ $curr }	eq	"$default_type" ) {


		&roman_num_1();
		&roman_num_2();
		&roman_num_3();
		&roman_num_4();
#		&roman_num_5();
		&roman_num_6();
	}


}
#**************************************************************#
sub roman_num_merge_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&roman_num_merge_1();

} # end sub
#**************************************************************#
# roman_num_1
#
# Intervals
# Example:	Läs sidan xv-xix.
#
#**************************************************************#
sub roman_num_1 {

	if (exists ( $ort{ $next_4 } )) {
		
#		print "
#		-----------
#		roman_num_1
#		Curr: $ort{ $curr }
#		Next1: $pos{ $next_1 }
#		Next2: $ort{ $next_2 }
#		Next3: $ort{ $next_3 }
#		Next4: $ort{ $next_4 }
#		\n"; 	
		
		if (	
			# Context
			(
				$ort{ $curr }	=~	/^[MLCDXVI][MLCDXVI]+$/
				||
				$ort{ $curr }	=~	/^[mlcdxvi][mlcdxvi]+$/
			)
			&&	
			$pos{ $next_1 }		eq	"DEL"
			&&
			$ort{ $next_2 }		eq	"-"
			&&
			$pos{ $next_3 }		eq	"DEL"
			&&	
			$ort{ $next_4 }		=~	/^[MLCDXVI]+$/i
			&&
			&isroman( $ort{ $curr } )
			&&
			&isroman( $ort{ $next_4 } )
		) {
			# Retag
			$type{ $curr }		=	"ROMAN NUM INTERVAL";
			$type{ $next_1 }	=	"ROMAN NUM INTERVAL";
			$type{ $next_2 }	=	"ROMAN NUM INTERVAL";
			$type{ $next_3 }	=	"ROMAN NUM INTERVAL";
			$type{ $next_4 }	=	"ROMAN NUM INTERVAL";
			
			$pos{ $curr }		=	"NUM CARD";
			$pos{ $next_4 }		=	"NUM CARD";
			
			$exp{ $next_2 }		=	"to";
		}

		
	} # end exists
	
	if (exists ( $ort{ $next_2 } )) {
		
#		print "
#		-----------
#		roman_num_1
#		Curr: $ort{ $curr }
#		Next1: $pos{ $next_1 }
#		Next2: $ort{ $next_2 }
#		\n"; 	
		
		if (	
			# Context
			(
				$ort{ $curr }	=~	/^[MLCDXVI][MLCDXVI]+$/
				||
				$ort{ $curr }	=~	/^[mlcdxvi][mlcdxvi]+$/
			)
			&&	
			$ort{ $next_1 }		eq	"-"
			&&
			(
				$ort{ $next_2 }		=~	/^[MLCDXVI]+$/i
				||
				$ort{ $next_2 }		=~	/^[mlcdxvi]+$/
			)
			&&
			&isroman( $ort{ $curr } )
			&&
			&isroman( $ort{ $next_2 } )
		) {
			# Retag
			$type{ $curr }		=	"ROMAN NUM INTERVAL";
			$type{ $next_1 }	=	"ROMAN NUM INTERVAL";
			$type{ $next_2 }	=	"ROMAN NUM INTERVAL";
			
			$pos{ $curr }		=	"NUM CARD";
			$pos{ $next_2 }		=	"NUM CARD";
			
			$exp{ $next_1 }		=	"to";
		}

		
		
		

	} # end exists
}
#**************************************************************#
# roman_num_2
#
# Safe roman numbers (not "I", "VI" or "X" preceded by 
# typical section word as "chapter" or "part".
# "I", "VI" and "X" only allowed if preceeded by "notreferens".
#
#**************************************************************#
sub roman_num_2 {

	if (exists ( $ort{ $prev_2 } )) {
		
#		print "
#		-----------
#		roman_num_2
#		Prev2: $ort{ $prev_2 } 
#		Prev1: $pos{ $prev_1 } 
#		Curr: $ort{ $curr }
#		\n"; 	
		
		if (	
			# Context
			(
			$ort{ $curr }		=~	/^$safe_roman$/i
			&&	
			$pos{ $prev_1 }		eq	"DEL"
			&&
			$ort{ $prev_2 }		=~	/$roman_words/i
			) || (
			$ort{ $curr }		=~	/^[IVX]+$/i
			&&
			$pos{ $prev_1 }		eq	"DEL"
			&&
			$ort{ $prev_2 }		=~	/^notreferens$/i
			&&
			&isroman( $ort{ $curr } )
			)
		) {
			
			
			# Retag
			$type{ $curr }		=	"ROMAN NUM";
			$pos{ $curr }		=	"NUM CARD";
		}

		
		

	} # end exists
}
#**************************************************************#
# roman_num_3
#
# Example:	IV Påvekyrkans uppgång och fall
#**************************************************************#
sub roman_num_3 {

	# First in string ($prev_1 does not exist).
	if (exists ( $ort{ $next_2 } ) && not (exists ( $ort{ $prev_1 } ))) {

#		print "\n
#		------------------
#		roman_num_3
#		Curr: $ort{ $curr }
#		Next1: $ort{ $next_1 }
#		Next2: $ort{ $next_2 }
#		\n";

		if (
			
			$ort{ $curr }	=~	/^[XVI][XVI]+$/
			&&
			$pos{ $next_1 }	eq	"DEL"
			&&
			$ort{ $next_2 }	=~	/^[A-ZÅÄÖ][a-zåäö]/
			&&
			&isroman( $ort{ $curr } )
		) {
			# Retag
			$type{ $curr }		=	"ROMAN NUM";
			$pos{ $curr }		=	"NUM CARD";
			$morphology{ $curr }	=	"-";
			
		}

	} # end exists
}
#**************************************************************#
# roman_num_4
#
# Example:	I. Påvekyrkans uppgång och fall
#**************************************************************#
sub roman_num_4 {

	# First in string ($prev_1 does not exist).
	if (exists ( $ort{ $next_3 } ) && not (exists ( $ort{ $prev_1 } ))) {

#		print "\n
#		------------------
#		roman_num_4
#		Curr: $ort{ $curr }
#		Next1: $ort{ $next_1 }
#		Next2: $ort{ $next_2 }
#		Next3: $ort{ $next_3 }
#		\n";

		if (
			
			$ort{ $curr }	=~	/^[XVI]+$/
			&&
			$ort{ $next_1 }	eq	"."
			&&
			$pos{ $next_2 }	eq	"DEL"
			&&
			$ort{ $next_3 }	=~	/^[A-ZÅÄÖ][a-zåäö]/
			&&
			&isroman( $ort{ $curr } )
		) {
			# Retag
			$type{ $curr }		=	"ROMAN NUM";
			$pos{ $curr }		=	"NUM CARD";
			$morphology{ $curr }	=	"-";
		}

	} # end exists
}
#**************************************************************#
# roman_num_5
#
# Only roman number in string, but more than one letter.
# Example:	II
#**************************************************************#
sub roman_num_5 {

	# First or last in string ($prev_1 and $next_1 do not exist).
	if (not (exists ( $ort{ $prev_1 } ) && not (exists ( $ort{ $next_1 } )))) {

#		print "\n
#		------------------
#		roman_num_5
#		Curr: $ort{ $curr }
#		\n";

		if (
			# Context
			not ( exists ( $ort{ $prev_1 } ) )
			&&
			not ( exists ( $ort{ $next_1 } ) )
			&&
			(
				$ort{ $curr }	=~	/^[MLCDXVI][MLCDXVI]+$/
				||
				$ort{ $curr }	=~	/^[mlcdxvi][mlcdxvi]+$/
			)
			&&
			&isroman( $ort{ $curr } )
			
			&&
			# Restrictions
			$ort{ $curr }	!~	/^(div|liv|lix|mix|mdi|mmi|mm+)$/i
			

		) {
			# Retag
			$type{ $curr }		=	"ROMAN NUM";
			$pos{ $curr }		=	"NUM CARD";
			$morphology{ $curr }	=	"-";
		}
	} # end exists
}
#**************************************************************#
# roman_num_6
#
# Roman number preceeded by name.
# Example:	Karl XII
#**************************************************************#
sub roman_num_6 {
	
	if (exists ( $ort{ $prev_2 } )) {
		
		if (
			# Context
			(
				$ort{ $curr }	=~	/^[MLCDXVI][MLCDXVI]+$/
				||
				$ort{ $curr }	=~	/^[mlcdxvi][mlcdxvi]+$/
			)
			&&
			&isroman( $ort{ $curr } )
			&&
			$pos{ $prev_1 }	eq	"DEL"
			&&
			$pos{ $prev_2 }	=~	/(^|\|)PM/		# One of the alternatives are "PM".
		) {
			# Retag
			$exp{ $curr }	=	"den <NUM ORD>";
			$type{ $curr }	=	"ROMAN NUM";
			$pos{ $curr }	=	"NUM ORD";
		}
		
	} # end exists
}
#**************************************************************#
# roman_num_merge_1
#
# Roman numbers as ordinals.
# Example:	XII:s
#
#**************************************************************#
sub roman_num_merge_1 {

	if (exists ( $ort{ $next_2 } )) {
		
#		print "
#		-----------
#		roman_num_merge_1
#		Curr: $ort{ $curr }
#		Next1: $pos{ $next_1 } 
#		Next2: $ort{ $next_2 } 
#		\n";
		
		if (	
			# Context
			$ort{ $curr }		=~	/^[XVI]+$/i
			&&	
			$ort{ $next_1 }		eq	":"
			&&
			$ort{ $next_2 }		eq	"s"
			&&
			&isroman( $ort{ $curr } )
		) {
			# Merge fields
			$ort{ $curr }		.=	"$ort{ $next_1 }$ort{ $next_2 }";
			
			delete( $ort{ $next_1 } );
			delete( $ort{ $next_2 } );

			# Retag			
			$type{ $curr }		=	"ROMAN NUM";
			$pos{ $curr }		=	"NUM ORD";
			
			# Renumber fields
			&renumber_fields();			

		}
	} # end exists
}
#**************************************************************#
# isroman
#
# Validating roman numbers.
#
#**************************************************************#
sub isroman {
    my($arg) = shift;
    $arg ne '' and
      $arg =~ /^(?: M{0,3})
                (?: D?C{0,3} | C[DM])
                (?: L?X{0,3} | X[LC])
                (?: V?I{0,3} | I[VX])$/ix;
}
#**************************************************************#
1;
