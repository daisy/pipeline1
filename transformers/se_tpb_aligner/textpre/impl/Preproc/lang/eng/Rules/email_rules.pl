#!/usr/bin/perl -w


sub email_subs {
	
	my $index_list = shift;
	($prev_10,$prev_9,$prev_8,$prev_7,$prev_6,$prev_5,$prev_4,$prev_3,$prev_2,$prev_1,$curr,$next_1,$next_2,$next_3,$next_4,$next_5,$next_6,$next_7,$next_8,$next_9,$next_10) = split/\|/,$index_list;

	&email_1();
	
}

#**************************************************************#
# email_1
# Example:	x.y@hotmail.com
#
#**************************************************************#
sub email_1 {
	
	if (exists ($ort{ $next_3 } ) && exists($ort{ $prev_1} )) {

#		print "\n\n------------------\n
#		email_1
#		Curr: $ort{ $curr }
#		Prev1: $pos{ $prev_1 }
#		Next1: $pos{ $next_1 }
#		Next2: $ort{ $next_2 }
#		Next3: $ort{ $next_3 }
#		\n\n";

		if (
			# Context
			$ort{ $curr }		eq	"@"
			&&
			$pos{ $prev_1 }		ne	"DEL"
			&&
			$pos{ $next_1 }		ne	"DEL"
			&&
			$ort{ $next_2 }		eq	"."
			&&
			$ort{ $next_3 }		ne	"DEL"
			
		) {	
			# Retag "@"
			$type{ $curr }		= 	"EMAIL";
			
			my @index_list		=	split/\|/,$index_list;
			@index_list		=	sort(@index_list);
			my @reversed_index_list	=	reverse(@index_list);



			# Tag left context fields as "EMAIL" until next "DEL"
			my $saved_counter 	=	$curr;
			my $pos_tester		=	0;
			my $lc_check		=	0;
			
#			$counter = $#reversed_index_list + 1;
			
			foreach my $lc (@reversed_index_list) {

				if ($lc eq "$saved_counter") {
					$lc_check = 1;
				}
				
#			print "\n\nRRRRRNNNNNNNNLL $lc\t$ort{$lc}\t$pos{$lc}\t$type{$lc}\n";
#			print "CHECK $lc_check\tSC $saved_counter\tLC: $lc\n\n";
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
								
						$type{ $lc }	=	"EMAIL";
					} else {
						$pos_tester	=	1;
					}
				}
				
#				print "RRRRRNNNNNNNNLL $lc\t$ort{$lc}\t$pos{$lc}\t$type{$lc}\n";

			} # end foreach @reversed_index_list


				

			# Tag right context fields as "EMAIL" until next "DEL"
			$pos_tester		=	0;
			my $rc_check		=	0;
			my $counter		=	0;
			
			
			foreach my $rc (@index_list) {
			
				if ($rc eq "$saved_counter") {
					$rc_check = 1;
				}

				if (
					$rc		=~	/\d/
					&&
					$rc_check	==	1
					&&
					$pos_tester	==	0
				) {
					if ( 
						$pos{ $index_list[ $counter ] }	=~	/^(?:(?:MAJ|MIN)[ _]DEL|QUOTE)$/
						&&
						(
							$index_list[ $counter + 1 ]		!~	/\d/
							||
							$pos{ $index_list[ $counter + 1 ] }	=~	"DEL"
						)
					) {	
						$pos_tester	=	1;			
					
					} elsif (
						$pos{ $rc }	ne	"DEL"
					) {
								
						$type{ $rc }	=	"EMAIL";
						
						# Domain							
						if ( $ort{ $rc }	=~	/^$domain_list$/i ) {
							&domain_rule($rc);
						}
			
					} else {
						$pos_tester	=	1;
					}
#						print "\n\n--------\nRC: $rc\nORT: $ort{$rc}\nPT: $pos_tester\nCOU: $counter\t$saved_counter\nP1: $pos{ $index_list[ $counter ] }\nP2: $index_list[ $counter + 1 ]\n\n";


				}
				$counter++;
			} # end foreach @index_list

		}
	} # end exists
}
#**************************************************************#
sub domain_rule {
	my $loc = shift;
	
	$type{ $loc }	.=	"|DOMAIN";
							
	my $o = lc($ort{ $loc });
	$transcription{ $loc }	=	$domain_list{ $o };
	$lang{ $loc }		=	"swe";
	$morphology{ $loc }	=	"-";
	$pos{ $loc }		=	"DOMAIN";
}
#**************************************************************#

1;