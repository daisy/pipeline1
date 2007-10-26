#!/usr/bin/perl -w


#**************************************************************#
sub time_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&time_1();	
	&time_2();	

}
#**************************************************************#
# time_1
# Example:	12 o'clock
#		11 p.m.
#
#**************************************************************#
sub time_1 {
	
	if (exists ($ort{ $next_2 } )) {
		
#		print "\n\n---------
#		time_1
#		Curr: $ort{$curr} __
#		Curr: $type{$curr} __
#		Next1: $pos{$next_1}
#		Next2: $ort{$next_2}
#		Next3: $ort{$next_3}
#		Next4: $ort{$next_4}
#		";
		
		
		if (
			# Context
			$type{ $curr }	eq	$default_type
			&&
			$ort{ $curr }	=~	/^\d+$/io		# 12
			&&
			$pos{ $next_1 }	eq	"DEL"		
			&&
			$ort{ $next_2 }	=~	/^(o\'clock|[pa]\.?m\.?)$/io		# o'clock
					
		) {
			# Retag
			$type{ $curr }		=	"TIME";
			$type{ $next_1 }	=	"TIME";
			$type{ $next_2 }	=	"TIME";

			if ( $ort{ $next_2 }	=~	/^([pa]\.?m\.?)$/io ) {
				$type{ $next_2 }	.=	"|ABBR";
			}
			
			$pos{ $curr }		=	"NUM CARD";
		}
	} # end exists
}
#**************************************************************#
# time_2
# Example:	11:45
#		
#
#**************************************************************#
sub time_2 {
	
	if (exists ($ort{ $next_2 } )) {
		
#		print "\n\n---------
#		time_1
#		Curr: $ort{$curr} __
#		Curr: $type{$curr} __
#		Next1: $pos{$next_1}
#		Next2: $ort{$next_2}
#		Next3: $ort{$next_3}
#		Next4: $ort{$next_4}
#		";
		
		
		if (
			# Context
			$type{ $curr }	eq	$default_type
			&&
			$ort{ $curr }	=~	/^\d\d?$/io		# 11
			&&
			$ort{ $next_1 }	eq	":"			# :
			&&
			$ort{ $next_2 }	=~	/^\d\d$/io		# 45
					
		) {
			# Retag
			$type{ $curr }		=	"TIME";
			$type{ $next_1 }	=	"TIME";
			$type{ $next_2 }	=	"TIME";
			
			$pos{ $curr }		=	"NUM CARD";
			$pos{ $next_2 }		=	"NUM CARD";
		}
	} # end exists
}
#**************************************************************#
1;
