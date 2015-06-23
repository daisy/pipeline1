#!/usr/bin/perl -w



sub filename_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&filename_1();
	&filename_2();
	
}

#**************************************************************#
# filename_1
# Example:	C:\My documents\file.txt
#
#**************************************************************#
sub filename_1 {
	
	if (exists ($ort{ $next_3 } )) {

#		print "\n\n------------------\n
#		email_1
#		Curr: $ort{ $curr }
#		Next1: $ort{ $next_1 }
#		Next2: $ort{ $next_2 }
#		Next3: $ort{ $next_3 }
#		\n\n";


		if (
			# Context
			$ort{ $curr }		=~	/^[A-Z]$/
			&&
			$ort{ $next_1 }		eq	":"
			&&
			$ort{ $next_2 }		=~	/^[\/\\]$/
			
		) {	
			# Retag start
			$type{ $curr }		= 	"FILENAME";
			$type{ $next_1 }	= 	"FILENAME";
			$type{ $next_2 }	= 	"FILENAME";
			
			my @index_list		=	split/\|/,$index_list;
			@index_list		=	sort(@index_list);
	

			# Tag right context fields as "FILENAME" until next "DEL"
			$pos_tester		=	0;
			my $counter		=	0;
	
			foreach my $rc (@index_list) {
			
				if (
					$rc		=~	/\d/
					&&
					$pos_tester	==	0
				) {
					if ( 
						$pos{ $index_list[ $counter ] }	=~	/^(?:(?:MAJ|MIN)[ _]DEL|QUOTE)$/
						&&
						not ( exists ( $ort{ $index_list[ $counter+1 ] } ))
						

					) {	
						$pos_tester	=	1;			

			
					} elsif (
						$pos{ $rc }	ne	"DEL"
					) {
								
						$type{ $rc }	=	"FILENAME";

					} else {
						$pos_tester	=	1;
					}

#					print "\n\n--------yy\nTYP: $type{ $rc }\nRC: $rc\nORT: $ort{$rc}\nPT: $pos_tester\nCOU: $counter\t$saved_counter\nP1: $pos{ $index_list[ $counter ] }\nP2: $index_list[ $counter + 1 ]\n\n";


				}
				$counter++;
			} # end foreach @index_list

		}
	} # end exists
}
#**************************************************************#
# filename_2
#
# Filenames without path.
# Example:	preproc.pdf
#
#**************************************************************#
sub filename_2 {

	if (exists ($ort{ $prev_2 } )) {

		# 1. ".xxx"
		if (
			# Context
			$ort{ $prev_1 }		eq	"."
			&&
			$ort{ $curr }		=~	/^([a-z][a-z][a-z])$/i
			
		) {

			my $rule_apply		=	0;
			

			# 2a. $next_1 is end of string or blank
			if (
				not (exists ( $ort{ $next_1 } ))
				||
				$pos{ $next_1 }		eq	"DEL"
			) {
				$rule_apply = 1;
			
			# 2b. $next_1 is "." and $next_2 is end of string	
			} elsif (
				$ort{ $next_1 }		eq	"."
				&&
				not (exists ( $ort{ $next_2 } ))
			) {
				$rule_apply = 1;
			
			# 2c. $next_1 is "." and $next_2 is "DEL"
			} elsif (
				$ort{ $next_1 }		eq	"."
				&&
				$pos{ $next_2 }		eq	"DEL"
			) {
				$rule_apply = 1;
			}
				

			if ( $rule_apply == 1 ) {
			
				# Retag
				$type{ $prev_1 }	=	"FILENAME";
				$type{ $curr }		=	"FILENAME";
				
				# Check preceeding fields until "DEL".
				
				my @index_list		=	split/\|/,$index_list;
				@index_list		=	sort(@index_list);
				my @reversed_index_list	=	reverse(@index_list);


				# Tag left context fields as "EMAIL" until next "DEL"
				my $saved_counter 	=	$curr;
				my $pos_tester		=	0;
				my $lc_check		=	0;
			
			
				foreach my $lc (@reversed_index_list) {

					if ($lc eq "$saved_counter") {
						$lc_check = 1;
					}
				
					if (
						$lc =~ /\d/
						&&
						$lc_check	==	1
						&&
						$pos_tester	==	0
					) {
						
	#					print "POS $pos{$lc}\n\n";
						
						if (
							$pos{ $lc }	ne	"DEL"
						) {
									
							$type{ $lc }	=	"FILENAME";
						} else {
							$pos_tester	=	1;
						}
					}
					
	#				print "RRRRRNNNNNNNNLL $lc\t$ort{$lc}\t$pos{$lc}\t$type{$lc}\n";
	
				} # end foreach @reversed_index_list

				
			}
			
		}
	} # end exists
}
#**************************************************************#
1;
