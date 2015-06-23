#!/usr/bin/perl -w

#**************************************************************#
sub decimal_num_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&decimal_num_1();
	&decimal_num_2();

}
#**************************************************************#
# decimal_num_1
#
# Example:	21	,	14	-->	21,14
#		NUM	MIN_DEL	NUM	-->	NUM DEC
#**************************************************************#
sub decimal_num_1 {

	my $apply_rule	= 1;

	if (exists ($ort{ $next_2 } )) {

#		print "\n-----------
#		CURR: $ort{$curr}
#		N1: $ort{$next_1}
#		N2: $ort{$next_2}
#		P2: $ort{$prev_2}
#		P1: $ort{$prev_1}
#		\n";
	
		if (	
			# Context
			$ort{ $curr }	=~	/^\d+$/
			&&
			$ort{ $next_1 }	=~	/^[\,\.]$/
			&&
			$ort{ $next_2 }	=~	/^\d+$/
		) {
			# Restrictions
			if (exists ($ort{ $prev_2 } )) {
				if (
					$ort{ $prev_2 }		=~	/^\d+$/
					&&
					$ort{ $prev_1 }		=~	/^[\.\,]$/
				) {
					$apply_rule		= 0;
				}
			}
			
			if (exists ($ort{ $next_4 } )) {
				if (
				$ort{ $next_3 }		=~	/^[\.\,]$/
					&&
					$ort{ $next_4 }	=~	/^\d+$/
				) {
					$apply_rule	=	0;
				}
			}


			if ( $apply_rule	==	1 ) {
				# Retag
				$type{ $curr }		=	"NUM DEC";
				$type{ $next_1 }	=	"NUM DEC";	
				$type{ $next_2 }	=	"NUM DEC";

				$pos{ $curr }		=	"NUM CARD";
				$pos{ $next_1 }		=	"CHAR";	
				$pos{ $next_2 }		=	"NUM CARD";
			}
	
		}				

	} # end exists
}
#**************************************************************#
# decimal_num_2
#
# Fractions
#
# Example:	1½	3 ¼	¾
#
#**************************************************************#
sub decimal_num_2 {

	# With blanks
	if (exists ($ort{ $next_2 } )) {

#		print "\n-----------
#		CURR: $ort{$curr}
#		N1: $ort{$next_1}
#		N2: $ort{$next_2}
#		\n";
	
		if (	
			# Context
			$ort{ $curr }		=~	/^\d+$/		# 1
			&&
			$pos{ $next_1 }		eq	"DEL"		#
			&&
			$ort{ $next_2 }		=~	/^$fractions$/	# ½
		) {
			# Retag
			$type{ $curr }		=	"NUM FRAC";	
			$type{ $next_1 }	=	"NUM FRAC";	
			$type{ $next_2 }	=	"NUM FRAC";

			$pos{ $curr }		=	"NUM CARD";
			$pos{ $next_1 }		=	"DEL";
			$pos{ $next_2 }		=	"NUM CARD";
		}				

	} # end exists

	# Without blanks
	if (exists ($ort{ $next_1 } )) {

#		print "\n-----------
#		CURR: $ort{$curr}
#		N1: $ort{$next_1}
#		N2: $ort{$next_2}
#		\n";
	
		if (	
			# Context
			$ort{ $curr }		=~	/^\d+$/		# 1
			&&
			$ort{ $next_1 }		=~	/^$fractions$/	# ½
		) {
			# Retag
			$type{ $curr }		=	"NUM FRAC";	
			$type{ $next_1 }	=	"NUM FRAC";	

			$pos{ $curr }		=	"NUM CARD";
			$pos{ $next_1 }		=	"NUM CARD";
		}				

	} # end exists
	
	if (	
		# Context
		$ort{ $curr }		=~	/^$fractions$/
	) {
		# Retag
		$type{ $curr }		=	"NUM FRAC";	

		$pos{ $curr }		=	"NUM CARD";
	}				

}
#**************************************************************#
1;
